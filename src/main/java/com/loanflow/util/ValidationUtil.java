package com.loanflow.util;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanApplication;
import com.loanflow.enums.ApplicationStatus;
import com.loanflow.enums.EmiStatus;
import com.loanflow.enums.LoanStatus;
import com.loanflow.exception.BusinessRuleException;
import com.loanflow.exception.InvalidStatusTransitionException;

/**
 * Business-rule guards used inside service methods.
 * Each method throws a typed exception — the
 * GlobalExceptionHandler maps these to correct HTTP codes.
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    // ── Payment guards ─────────────────────────────────────────────

    /**
     * Prevents paying an EMI that is already PAID.
     * Called by PaymentService.simulatePayment() before creating
     * a Payment record.
     *
     * This is the service-layer guard — the DB-level guard is the
     * unique constraint on Payment.emiSchedule.
     */
    public static void ensureEmiNotAlreadyPaid(EmiSchedule emiSchedule) {
        if (emiSchedule.getStatus() == EmiStatus.PAID) {
            throw new BusinessRuleException(
                    "Installment " + emiSchedule.getInstallmentNumber()
                            + " is already marked as paid.");
        }
    }

    /**
     * Ensures the loan is ACTIVE before accepting any payment.
     * A CLOSED or DEFAULTED loan must not receive new payments
     * through the simulation flow.
     */
    public static void ensureLoanIsActive(Loan loan) {
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "Payments can only be made on ACTIVE loans. " +
                            "Current status: " + loan.getStatus());
        }
    }

    // ── Application guards ─────────────────────────────────────────

    /**
     * Prevents an officer from acting on an application that is
     * not in PENDING or UNDER_REVIEW status.
     * Called by LoanService.processDecision().
     */
    public static void ensureApplicationIsReviewable(
            LoanApplication application) {
        if (application.getStatus() != ApplicationStatus.PENDING
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new InvalidStatusTransitionException(
                    "Application " + application.getId()
                            + " cannot be reviewed. Current status: "
                            + application.getStatus());
        }
    }

    /**
     * Prevents a borrower from cancelling an application that
     * has already been reviewed (APPROVED or REJECTED).
     */
    public static void ensureApplicationIsCancellable(
            LoanApplication application) {
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessRuleException(
                    "Only PENDING applications can be cancelled.");
        }
    }
}

