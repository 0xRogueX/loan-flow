package com.loanflow.service.impl;

import com.loanflow.dto.request.AuditRequest;
import com.loanflow.dto.request.LoanApplicationRequest;
import com.loanflow.dto.response.LoanApplicationResponse;
import com.loanflow.entity.LoanApplication;
import com.loanflow.entity.user.Borrower;
import com.loanflow.entity.user.User;
import com.loanflow.enums.*;
import com.loanflow.exception.LoanLimitExceededException;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.integration.CreditBureauService;
import com.loanflow.integration.CreditBureauServiceImpl;
import com.loanflow.mapper.LoanApplicationMapper;
import com.loanflow.repository.LoanApplicationRepository;
import com.loanflow.service.LoanApplicationService;
import com.loanflow.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;


import com.loanflow.constants.LoanConstants;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.repository.LoanRepository;
import com.loanflow.service.AuditService;
import com.loanflow.service.DtiCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanRepository loanRepository;
    private final DtiCalculationService dtiCalculationService;
    private final CreditBureauService creditBureauService;
    private final AuditService auditService;
    private final LoanApplicationMapper loanApplicationMapper;
    private final ApplicationEventPublisher eventPublisher;

    //  APPLY — Phase 1 (DTI_initial only)
    @Override
    @Transactional
    public LoanApplicationResponse apply(LoanApplicationRequest request, User borrower) {
        Borrower currentBorrower = (Borrower) borrower;

        // Guard 1: max 3 active loans
        long activeLoans = loanRepository
                .countByBorrowerAndStatus(currentBorrower, LoanStatus.ACTIVE);
        if (activeLoans >= LoanConstants.MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(
                    "Maximum " + LoanConstants.MAX_ACTIVE_LOANS
                            + " active loans allowed. Current: " + activeLoans);
        }

        // Guard 2: no existing PENDING or UNDER_REVIEW application
        boolean hasPending = loanApplicationRepository
                .existsByBorrowerAndStatusIn(currentBorrower,
                        List.of(ApplicationStatus.PENDING, ApplicationStatus.UNDER_REVIEW));
        if (hasPending) {
            throw new BusinessRuleException(
                    "You already have a loan application under review.");
        }

        // 1. Fetch external EMI from Credit Bureau
        CreditBureauServiceImpl.ExternalDebtResult bureauResult = creditBureauService
                .fetchExternalEmi(currentBorrower.getPanNumber());

        BigDecimal externalEmi  = bureauResult.externalMonthlyEmi();
        String     bureauStatus = bureauResult.bureauStatus();
        // bureauStatus = "AVAILABLE" or "UNAVAILABLE" (LoanConstants)

        // 2. Fetch internal EMI (our own active loans)
        BigDecimal internalEmi = loanRepository
                .sumActiveMonthlyEmi(currentBorrower.getId())
                .orElse(BigDecimal.ZERO);

        // 3. Calculate DTI_initial
        // Formula: (internalEmi + externalEmi) / monthlyIncome × 100
        // This is Phase 1 — used for early rejection gate and strategy suggestion
        // DTI_final is calculated LATER in LoanService.processDecision()
        // after the officer sets the interest rate and the EMI is computed
        BigDecimal dtiInitial = dtiCalculationService.calculateInitialDti(
                internalEmi, externalEmi, request.getMonthlyIncome());

        log.info("Borrower {} — DTI_initial: {}%",
                currentBorrower.getId(), dtiInitial);

        // 4. Suggest strategy from DTI_initial
        // Returns null when DTI > 40% (high risk → auto-reject)
        // Returns FLAT / REDUCING / STEP_UP based on DTI range + tenure
        LoanStrategy suggested = dtiCalculationService.suggestStrategy(
                dtiInitial, request.getTenureMonths());

        // 5. Build application
        LoanApplication application = LoanApplication.builder()
                .borrower(currentBorrower)
                .requestedAmount(request.getRequestedAmount())
                .tenureMonths(request.getTenureMonths())
                .monthlyIncome(request.getMonthlyIncome())
                .existingMonthlyEmi(externalEmi)
                .calculatedDti(dtiInitial)
                .suggestedStrategy(suggested)
                .bureauStatus(BureauStatus.valueOf(bureauStatus))   // String: "AVAILABLE" or "UNAVAILABLE" -> Enum
                .build();

        // 6. Auto-reject if high risk (DTI > 40%)
        if (suggested == null) {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReason(
                    "Auto-rejected: DTI " + dtiInitial + "% exceeds 40% threshold.");

            application.setApplicationNumber(generateApplicationNumber());
            LoanApplication saved = loanApplicationRepository.save(application);

            auditService.log(AuditRequest.builder()
                    .entityType(EntityType.LOAN_APPLICATION)
                    .entityId(saved.getId())
                    .action("AUTO_REJECTED")
                    .newStatus("REJECTED")
                    .actorRole(Role.BORROWER)
                    .remarks("DTI: " + dtiInitial + "%")
                    .build());

//            eventPublisher.publishEvent(new LoanApplicationSubmittedEvent(saved));
            return loanApplicationMapper.toResponse(saved);
        }

        // 7. Save as PENDING
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationNumber(generateApplicationNumber());
        LoanApplication saved = loanApplicationRepository.save(application);

        auditService.log(AuditRequest.builder()
                .entityType(EntityType.LOAN_APPLICATION)
                .entityId(saved.getId())
                .action("SUBMITTED")
                .newStatus("PENDING")
                .performedBy(currentBorrower)
                .actorRole(currentBorrower.getRole())
                .build());

//        eventPublisher.publishEvent(new LoanApplicationSubmittedEvent(saved));

        log.info("Application {} saved with strategy: {}",
                saved.getId(), suggested);
        return loanApplicationMapper.toResponse(saved);
    }


    private String generateApplicationNumber() {
        Long seqVal = loanApplicationRepository.getNextApplicationSequence();

        if (seqVal == null)
            throw new IllegalStateException("Failed to generate application number sequence");

        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        return String.format("APP-%s-%06d", datePrefix, seqVal);
    }


    //  CANCEL — only PENDING
    @Override
    public void cancelApplication(String applicationNumber, User borrower) {

        LoanApplication application = loanApplicationRepository
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with number: " + applicationNumber));

        // Ownership Guard
        if (!application.getBorrower().getId().equals(borrower.getId())) {
            throw new UnauthorizedAccessException("You can only cancel your own application.");
        }

        // Status guard — only PENDING can be cancelled
        // UNDER_REVIEW means officer has started review — too late to cancel
        ValidationUtil.ensureApplicationIsCancellable(application);

        String oldStatus = application.getStatus().name();
        application.setStatus(ApplicationStatus.CANCELLED);
        loanApplicationRepository.save(application);

        auditService.log(AuditRequest.builder()
                .entityType(EntityType.LOAN_APPLICATION)
                .entityId(application.getId())
                .action("CANCELLED")
                .oldStatus(oldStatus)
                .newStatus("CANCELLED")
                .performedBy(borrower)
                .actorRole(Role.BORROWER)
                .remarks("Borrower cancelled their own application.")
                .build());

        log.info("Application {} successfully cancelled by user {}", applicationNumber, borrower.getEmail());
    }


    //  READ — officer pending queue
    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getPendingApplications() {
        List<LoanApplication> pending = loanApplicationRepository
                .findByStatusOrderByCreatedAtAsc(ApplicationStatus.PENDING);
        return loanApplicationMapper.toResponseList(pending);
    }


    // READ — borrower's own history
    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getMyApplications(User borrower) {
        List<LoanApplication> applications = loanApplicationRepository
                .findByBorrowerOrderByCreatedAtDesc(borrower);
        return loanApplicationMapper.toResponseList(applications);
    }


}
