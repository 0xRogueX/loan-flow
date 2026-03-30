package com.loanflow.listener;

import com.loanflow.entity.LoanApplication;
import com.loanflow.enums.ApplicationStatus;
import com.loanflow.enums.NotificationEventType;
import com.loanflow.event.*;
import com.loanflow.service.NotificationService;
import com.loanflow.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    // 1. Application submitted (PENDING or AUTO-REJECTED)
    @Async
    @EventListener
    public void handleApplicationSubmitted(LoanApplicationSubmittedEvent event) {
        LoanApplication app = event.getApplication();
        notificationService.send(
                app.getBorrower(),
                null,           // no loan at submission time
                NotificationEventType.APPLICATION_SUBMITTED,
                "Loan Application Received - " + app.getApplicationNumber(),
                "application-submitted",
                Map.of("borrowerName", app.getBorrower().getName(),
                        "amount", app.getRequestedAmount(),
                        "status", app.getStatus()));        // ApplicationStatus enum
    }

    // 2. Officer decision — APPROVED or REJECTED
    @Async
    @EventListener
    public void handleLoanDecision(LoanDecisionEvent event) {
        boolean approved = event.getDecision() == ApplicationStatus.APPROVED;
        if (approved) {
            notificationService.send(
                    event.getApplication().getBorrower(),
                    event.getLoan(),
                    NotificationEventType.LOAN_APPROVED,
                    "Your Loan " + event.getLoan().getLoanNumber() + " is Approved!",
                    "loan-approved",
                    Map.of(
                            "borrowerName", event.getApplication().getBorrower().getName(),
                            "loanId",       event.getLoan().getId().toString(),
                            "amount",       event.getLoan().getApprovedAmount(),
                            "strategy",     event.getLoan().getStrategy().name(),
                            "tenure",       event.getLoan().getTenureMonths(),
                            "monthlyEmi",   event.getLoan().getMonthlyEmi()
                    )
            );
        } else {
            notificationService.send(
                    event.getApplication().getBorrower(),
                    null,
                    NotificationEventType.LOAN_REJECTED,
                    "Update on Your Application - " + event.getApplication().getApplicationNumber(),
                    "loan-rejected",
                    Map.of(
                            "borrowerName", event.getApplication().getBorrower().getName(),
                            "reason", event.getRejectionReason() != null ? event.getRejectionReason() : ""
                    )
            );
        }
    }

    // 3. EMI payment confirmed
    @Async
    @EventListener
    public void handlePaymentReceived(PaymentReceivedEvent event) {
        notificationService.send(
                event.getPayment().getBorrower(),
                event.getPayment().getLoan(),
                NotificationEventType.EMI_PAID,
                "EMI Payment Confirmed - Installment " + event.getEmiSchedule().getInstallmentNumber(),
                "emi-paid",
                Map.of(
                        "borrowerName",  event.getPayment().getBorrower().getName(),
                        "installmentNo", event.getEmiSchedule().getInstallmentNumber(),
                        "amount",        event.getPayment().getPaidAmount(),
                        "loanId",        event.getPayment().getLoan().getId()
                )
        );
    }

    // 4. Payment reminder (3 days before due)
    @Async
    @EventListener
    public void handlePaymentReminder(PaymentReminderEvent event) {
        notificationService.send(
                event.getEmiSchedule().getLoan().getBorrower(),
                event.getEmiSchedule().getLoan(),
                NotificationEventType.PAYMENT_REMINDER,
                "Reminder: EMI Due in 3 Days - " + event.getEmiSchedule().getLoan().getLoanNumber(),
                "payment-reminder",
                Map.of(
                        "borrowerName", event.getEmiSchedule().getLoan().getBorrower().getName(),
                        "dueDate",      DateUtil.format(event.getEmiSchedule().getDueDate()),
                        "amount",       event.getEmiSchedule().getTotalEmiAmount(),
                        "loanId",       event.getEmiSchedule().getLoan().getId()
                )
        );
    }

    // 5. Overdue alert
    @Async
    @EventListener
    public void handleOverdueAlert(OverdueAlertEvent event) {

        // Total penalty = fixed flat fee (₹500) + accumulated daily charge
        // fixedPenaltyAmount is set on day 1; penaltyCharge accumulates after day 30
        BigDecimal totalPenalty = event.getTracker().getFixedPenaltyAmount()
                .add(event.getTracker().getPenaltyCharge() != null
                        ? event.getTracker().getPenaltyCharge()
                        : BigDecimal.ZERO);

        notificationService.send(
                event.getTracker().getBorrower(),
                event.getTracker().getLoan(),
                NotificationEventType.OVERDUE_ALERT,
                "Urgent: Overdue EMI - " + event.getTracker().getLoan().getLoanNumber(),
                "overdue-alert",
                Map.of(
                        "borrowerName", event.getTracker().getBorrower().getName(),
                        "dueDate",      DateUtil.format(event.getTracker().getDueDate()),
                        "penalty",      totalPenalty,
                        "daysOverdue",  event.getTracker().getDaysOverdue(),
                        "loanId",       event.getTracker().getLoan().getId()
                )
        );
    }

    // 6. Loan fully repaid and closed
    @Async
    @EventListener
    public void handleLoanClosed(LoanClosedEvent event) {
        notificationService.send(
                event.getLoan().getBorrower(),
                event.getLoan(),
                NotificationEventType.LOAN_CLOSED,
                "Congratulations! Loan " + event.getLoan().getLoanNumber() + " Fully Repaid",
                "loan-closed",
                Map.of("borrowerName", event.getLoan().getBorrower().getName(),
                        "loanId",      event.getLoan().getId()
                ));
    }
}

