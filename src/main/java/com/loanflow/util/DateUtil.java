package com.loanflow.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date arithmetic helpers.
 * All date logic lives here — schedulers and strategies
 * never compute dates inline.
 */
public final class DateUtil {

    private DateUtil() {}

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ── EMI due date generation ────────────────────────────────────

    /**
     * Computes the due date for installment N.
     *
     * Uses the disbursement date's day-of-month as the anchor so
     * due dates never drift. A loan disbursed on 2025-03-15 has
     * installment 1 due on 2025-04-15, installment 2 on 2025-05-15, etc.
     *
     * Edge case: if disbursedOn is the 31st, months with fewer days
     * (e.g. February) use the last valid day via plusMonths() semantics.
     *
     * Called once per installment inside every strategy's generateSchedule().
     *
     * @param disbursedOn     date the loan was disbursed
     * @param installmentNum  1-based installment index
     * @return due date for that installment
     */
    public static LocalDate emiDueDate(
            LocalDate disbursedOn, int installmentNum) {
        return disbursedOn.plusMonths(installmentNum);
    }

    // ── Scheduler helpers ──────────────────────────────────────────

    /**
     * Returns true if the given date is strictly before today.
     * Used by OverdueScheduler to find missed EMIs.
     *
     * @param date the EMI due date to check
     */
    public static boolean isBeforeToday(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Returns a date N days from today.
     * Used by PaymentReminderScheduler:
     *   daysFromToday(LoanConstants.PAYMENT_REMINDER_DAYS_BEFORE)
     *   → finds all EMIs due exactly 3 days from now.
     *
     * @param days number of days ahead
     */
    public static LocalDate daysFromToday(int days) {
        return LocalDate.now().plusDays(days);
    }

    /**
     * Number of days between two dates (always positive).
     * Used by OverdueMonitorService to set tracker.daysOverdue.
     *
     * @param from  earlier date (EMI due date)
     * @param to    later date (today)
     * @return days elapsed — 0 if same day
     */
    public static int daysBetween(LocalDate from, LocalDate to) {
        return (int) ChronoUnit.DAYS.between(from, to);
    }

    // ── Formatting ─────────────────────────────────────────────────

    /**
     * Formats a date for use in Thymeleaf email templates.
     * Example: 2025-04-15 → "15 Apr 2025"
     *
     * Used by NotificationService when building the email model.
     *
     * @param date the date to format
     * @return human-readable date string
     */
    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_FORMAT);
    }
}

