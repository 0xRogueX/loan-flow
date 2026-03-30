package com.loanflow.constants;

import java.math.BigDecimal;

/**
 * Central registry for every business-rule constant.
 * Never hardcode these values in services or strategies.
 */
public final class LoanConstants {

    private LoanConstants() {}

    // DTI thresholds
    // Used by: DtiCalculationService.suggestStrategy()

    /** DTI below this → FLAT_RATE_LOAN suggested */
    public static final BigDecimal DTI_LOW_THRESHOLD =
            new BigDecimal("20.00");

    /** DTI between LOW and MID → REDUCING_BALANCE or STEP_UP */
    public static final BigDecimal DTI_MID_THRESHOLD =
            new BigDecimal("40.00");

    /** Tenure < this (months) → REDUCING_BALANCE; ≥ this → STEP_UP_EMI */
    public static final int STEP_UP_TENURE_THRESHOLD = 24;

    // Loan limits
    // Used by: LoanApplicationService.apply()

    /** A borrower may not hold more than this many active loans */
    public static final int MAX_ACTIVE_LOANS = 3;

    /** Maximum single loan amount a borrower may request */
    public static final BigDecimal MAX_LOAN_AMOUNT =
            new BigDecimal("50000000.00");

    /** Minimum loan tenure in months */
    public static final int MIN_TENURE_MONTHS = 1;

    /** Maximum loan tenure in months (30 years) */
    public static final int MAX_TENURE_MONTHS = 360;

    // Interest rate limits
    // Used by: LoanDecisionRequest validation, LoanService

    /** Maximum interest rate an officer may set (per annum) */
    public static final BigDecimal MAX_INTEREST_RATE_PA =
            new BigDecimal("36.00");

    // Step-Up EMI
    // Used by: StepUpEmiStrategy.generateSchedule()

    /**
     * Annual EMI step-up multiplier.
     * Each new calendar year, EMI = previous-year EMI × STEP_UP_ANNUAL_RATE.
     * 1.05 = 5% increase per year.
     */
    public static final BigDecimal STEP_UP_ANNUAL_RATE =
            new BigDecimal("1.05");

    // Overdue & penalty
    // Used by: OverdueMonitorService.scanAndMarkOverdue()

    /** Flat penalty amount applied per missed EMI (in INR) */
    public static final BigDecimal LATE_FEE_FLAT_AMOUNT =
            new BigDecimal("500.00");

    // Daily penalty rate (0.05% per day)
    public static final BigDecimal OVERDUE_DAILY_PENALTY_RATE =
            new BigDecimal("0.0005");

    // Grace period before daily penalty applies
    public static final int PENALTY_GRACE_DAYS = 30;

    // Days after which loan becomes DEFAULTED (NPA standard)
    public static final int DEFAULT_THRESHOLD_DAYS = 90;

    /**
     * Number of days a loan stays DEFAULTED before transitioning
     * to WRITTEN_OFF status.
     */
    public static final int WRITTEN_OFF_DAYS = 180;

    // Notification
    // Used by: NotificationRetryScheduler, PaymentReminderScheduler

    /** Maximum send attempts before a notification stays FAILED */
    public static final int MAX_NOTIFICATION_RETRIES = 3;

    /**
     * Days before EMI due date to send a payment reminder.
     * PaymentReminderScheduler queries: dueDate = today + PAYMENT_REMINDER_DAYS_BEFORE
     */
    public static final int PAYMENT_REMINDER_DAYS_BEFORE = 3;

    // Credit Bureau
    // Used by: LoanApplicationService, LoanApplication entity

    /** Value stored in LoanApplication.bureauStatus when report was fetched */
    public static final String BUREAU_STATUS_AVAILABLE   = "AVAILABLE";

    /** Value stored in LoanApplication.bureauStatus when bureau was down */
    public static final String BUREAU_STATUS_UNAVAILABLE = "UNAVAILABLE";

    // Audit actor roles
    // Used by: AuditService.log() calls across all services

    public static final String ACTOR_BORROWER      = "BORROWER";
    public static final String ACTOR_LOAN_OFFICER  = "LOAN_OFFICER";
    public static final String ACTOR_ADMIN         = "ADMIN";
    public static final String ACTOR_SYSTEM        = "SYSTEM";
}

