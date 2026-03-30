package com.loanflow.service.impl;

import com.loanflow.dto.request.AuditRequest;
import com.loanflow.dto.request.PaymentSimulationRequest;
import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.entity.Payment;
import com.loanflow.entity.user.User;
import com.loanflow.enums.*;
import com.loanflow.event.PaymentReceivedEvent;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.mapper.PaymentMapper;
import com.loanflow.repository.EmiScheduleRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.repository.OverdueTrackerRepository;
import com.loanflow.repository.PaymentRepository;
import com.loanflow.security.SecurityUtils;
import com.loanflow.service.AuditService;
import com.loanflow.service.LoanService;
import com.loanflow.service.PaymentService;
import com.loanflow.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final EmiScheduleRepository emiScheduleRepository;
    private final PaymentRepository paymentRepository;
    private final OverdueTrackerRepository overdueTrackerRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;
    private final AuditService auditService;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public PaymentResponse simulatePayment(PaymentSimulationRequest request, User borrower) {

        EmiSchedule emi = emiScheduleRepository
                .findById(request.getEmiScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EMI Schedule ot found: " + request.getEmiScheduleId()));

        // Guard 1: EMI must not already be paid
        ValidationUtil.ensureEmiNotAlreadyPaid(emi);

        // Guard 2: Loan must be ACTIVE
        ValidationUtil.ensureLoanIsActive(emi.getLoan());

        // capture old status for audit log
        String oldEmiStatus = emi.getStatus().name();

        // mark EMI as paid
        emi.setStatus(EmiStatus.PAID);
        emi.setPaidAt(LocalDateTime.now());
        emiScheduleRepository.save(emi);

        // create payment record
        Payment payment = new Payment();
        payment.setEmiSchedule(emi);
        payment.setLoan(emi.getLoan());
        payment.setBorrower(borrower);
        payment.setPaidAmount(emi.getTotalEmiAmount());
        payment.setPaymentMode(PaymentMode.SIMULATION);
        payment.setPaidAt(LocalDateTime.now());
        payment.setReceiptNumber(generateReceiptNumber());
        Payment saved = paymentRepository.save(payment);

        // if emi was overdue - resolve the tracker and penalty
        if (EmiStatus.OVERDUE.name().equals(oldEmiStatus)) {
            overdueTrackerRepository.findByEmiSchedule(emi)
                    .ifPresent(tracker -> {
                        tracker.setResolvedAt(LocalDateTime.now());
                        tracker.setPenaltyStatus(PenaltyStatus.SETTLED);
                        overdueTrackerRepository.save(tracker);

                        // decrement overdue count on loan
                        emi.getLoan().setOverDueCount(
                                Math.max(0, emi.getLoan().getOverDueCount() - 1));
                        loanRepository.save(emi.getLoan());
                    });
        }

        auditService.log(AuditRequest.builder()
                .entityType(EntityType.EMI_SCHEDULE)
                .entityId(emi.getId())
                .action("MARKED_PAID")
                .oldStatus(oldEmiStatus)
                .newStatus(EmiStatus.PAID.name())
                .performedBy(borrower)
                .actorRole(borrower.getRole())
                .remarks("Payment simulated. Amount: " + emi.getTotalEmiAmount())
                .build());

        // fire event -> listener sends EMI_PAID email
        applicationEventPublisher.publishEvent(new PaymentReceivedEvent(saved, emi));

        // check all emis paid -> loan closed
        loanService.closeLoanIfCompleted(emi.getLoan());

        log.info("Payment simulated for installment {} of loan {}",
                emi.getInstallmentNumber(), emi.getLoan().getId());

        return paymentMapper.toResponse(saved);
    }


    private String generateReceiptNumber() {
        Long seqVal = paymentRepository.getNextReceiptSequence();

        if (seqVal == null)
            throw new IllegalStateException("Failed to generate receipt number sequence");

        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        return String.format("RCP-%s-%05d", datePrefix, seqVal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByLoanNumber(String loanNumber) {

        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with number: " + loanNumber));

        if (!securityUtils.hasRole("LOAN_OFFICER")) {
            // Only the borrower (or an Admin) can view payments for this loan
            if (!securityUtils.isOwner(loan.getBorrower().getId())) {
                throw new UnauthorizedAccessException(
                        "Access Denied: You do not have permission to view payments for this loan.");
            }
        }

        List<Payment> payments = paymentRepository.findByLoan_LoanNumberOrderByPaidAtDesc(loanNumber);

        return payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

}
