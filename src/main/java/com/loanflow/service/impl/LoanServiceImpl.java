package com.loanflow.service.impl;

import com.loanflow.dto.request.AuditRequest;
import com.loanflow.dto.request.LoanDecisionRequest;
import com.loanflow.dto.response.LoanResponse;
import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanApplication;
import com.loanflow.entity.user.User;
import com.loanflow.enums.*;
import com.loanflow.event.LoanClosedEvent;
import com.loanflow.event.LoanDecisionEvent;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.mapper.LoanMapper;
import com.loanflow.repository.EmiScheduleRepository;
import com.loanflow.repository.LoanApplicationRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.service.*;
import com.loanflow.strategy.EmiCalculationStrategy;
import com.loanflow.strategy.LoanStrategyFactory;
import com.loanflow.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final EmiScheduleService emiScheduleService;
    private final DtiCalculationService dtiCalculationService;
    private final LoanStatusTransitionService loanStatusTransitionService;
    private final LoanStrategyFactory loanStrategyFactory;
    private final AuditService auditService;
    private final LoanMapper loanMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    // officer approve or reject loan application
    public LoanResponse processDecision(
            UUID applicationId, LoanDecisionRequest request, User officer) {

        // 1. Fetch application and validate
        LoanApplication application = loanApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Application not found: " + applicationId));

        ValidationUtil.ensureApplicationIsReviewable(application);

        application.setReviewedBy(officer);
        application.setReviewedAt(LocalDateTime.now());

        String oldStatus = application.getStatus().name();

        if(!request.getApproved()) {
            // reject application
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReason(request.getRejectionReason());
            loanApplicationRepository.save(application);

            auditService.log(AuditRequest.builder()
                            .entityType(EntityType.LOAN_APPLICATION)
                            .entityId(application.getId())
                            .action("REJECTED")
                            .oldStatus(oldStatus)
                            .newStatus(ApplicationStatus.REJECTED.name())
                            .performedBy(officer)
                            .actorRole(officer.getRole())
                            .remarks(request.getRejectionReason())
                            .build());

            eventPublisher.publishEvent(
                    new LoanDecisionEvent(null, application,
                            ApplicationStatus.REJECTED, request.getRejectionReason()));

            return null;   // no loan created on rejection
        }

        // approve application
        LoanStrategy finalStrategy = request.getOverrideStrategy() != null
                ? request.getOverrideStrategy()
                : application.getSuggestedStrategy();

        if (finalStrategy == null) {
            throw new BusinessRuleException("No valid loan strategy available");
        }

        // calculate internal and external emi before saved loan for repeat emi
        BigDecimal internalEmi = loanRepository
                .sumActiveMonthlyEmi(application.getBorrower().getId())
                .orElse(BigDecimal.ZERO);
        BigDecimal externalEmi = application.getExistingMonthlyEmi();


        application.setFinalStrategy(finalStrategy);
        application.setStatus(ApplicationStatus.APPROVED);
        loanApplicationRepository.save(application);

        // create loan entity
        Loan loan = new Loan();
        loan.setLoanNumber(generateLoanNumber());
        loan.setApplication(application);
        loan.setBorrower(application.getBorrower());
        loan.setApprovedBy(officer);
        loan.setApprovedAmount(application.getRequestedAmount());
        loan.setInterestRatePerAnnum(request.getInterestRatePerAnnum());
        loan.setTenureMonths(application.getTenureMonths());
        loan.setStrategy(finalStrategy);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setDisbursedAt(LocalDateTime.now());

        Loan savedLoan = loanRepository.save(loan);

        // calculate base emi using strategy
        EmiCalculationStrategy strategy = loanStrategyFactory.resolve(finalStrategy);
        BigDecimal baseEmi = emiScheduleService.generateSchedule(savedLoan, strategy);  // this baseEmi is new loan emi

        savedLoan.setMonthlyEmi(baseEmi);

        // calculate final dti for officer context
        BigDecimal dtiFinal = dtiCalculationService.calculateFinalDti(
                internalEmi,
                externalEmi,
                application.getMonthlyIncome(),
                baseEmi);

//        // Enforce the 40% DTI Rule
//        if (dtiFinal.compareTo(new BigDecimal("40.00")) > 0) {
//            throw new BusinessRuleException("Cannot approve: Final DTI with new loan exceeds 40% (Calculated: " + dtiFinal + "%). Please reject or reduce loan amount.");
//        }

        log.info("Loan {} - DTI_final: {}%", savedLoan.getId(), dtiFinal);

        loanRepository.save(savedLoan);

        // audit for loan application
        auditService.log(AuditRequest.builder()
                .entityType(EntityType.LOAN_APPLICATION)
                .entityId(application.getId())
                .action("APPROVED")
                .oldStatus(oldStatus)
                .newStatus(ApplicationStatus.APPROVED.name())
                .performedBy(officer)
                .actorRole(officer.getRole())
                .remarks("Strategy: " + finalStrategy + ", DTI_final: " + dtiFinal + "%")
                .build());

        // audit for loan creation
        auditService.log(AuditRequest.builder()
                .entityType(EntityType.LOAN)
                .entityId(savedLoan.getId())
                .action("CREATED")
                .newStatus(LoanStatus.ACTIVE.name())
                .performedBy(officer)
                .actorRole(officer.getRole())
                .remarks("Loan created from application " + application.getId())
                .build());

        eventPublisher.publishEvent(
                new LoanDecisionEvent(savedLoan, application,
                        ApplicationStatus.APPROVED, null));


        return loanMapper.toResponse(savedLoan);
    }

    private String generateLoanNumber() {
        Long seqVal = loanRepository.getNextLoanSequence();

        if (seqVal == null)
            throw new IllegalStateException("Failed to generate loan number sequence");

        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.format("LN-%s-%06d", datePrefix, seqVal);
    }

    @Override
    public void closeLoanIfCompleted(Loan loan) {
        Long unpaidCount = emiScheduleRepository
                .countByLoanAndStatusNot(loan, EmiStatus.PAID);

        if (unpaidCount == 0) {
            loanStatusTransitionService.transition(
                    loan, LoanStatus.CLOSED, null, "All EMIs paid");
            loan.setClosedAt(LocalDateTime.now());
            loanRepository.save(loan);

            eventPublisher.publishEvent(new LoanClosedEvent(loan));
            log.info("Loan {} closed as all EMIs are paid", loan.getId());
        }
    }

    @Override
    public List<LoanResponse> getMyLoans(User borrower) {
        List<Loan> loans = loanRepository.findByBorrowerOrderByCreatedAtDesc(borrower);
        return loanMapper.toResponseList(loans);
    }

    @Override
    public Loan findById(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));
    }

}
