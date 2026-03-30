package com.loanflow.util;

import com.loanflow.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Monetary calculation helpers.
 * All rounding uses HALF_UP — the banking standard.
 * Never call setScale() directly in strategy classes;
 * always delegate to these methods.
 */
public final class MoneyUtil {

    private MoneyUtil() {}

    // ── Rounding ───────────────────────────────────────────────────

    /**
     * Round to 2 decimal places using HALF_UP.
     * Called on every principal, interest, and EMI amount
     * produced by the three strategy classes.
     */
    public static BigDecimal roundHalfUp(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * High-precision intermediate calculation (10 decimal places).
     * Used inside the EMI formula before the final round.
     * Avoids compounding rounding error across 360 installments.
     */
    public static BigDecimal roundPrecision(BigDecimal value) {
        return value.setScale(10, RoundingMode.HALF_UP);
    }

    // ── Final installment adjustment ───────────────────────────────

    /**
     * Adjusts the LAST installment so the loan balance reaches exactly ZERO.
     *
     * Why needed: floating-point rounding across N installments
     * leaves a residual balance (typically ±1 paisa). Without this
     * adjustment the loan never technically closes.
     *
     * Called by all three strategy classes on installment N == tenureMonths.
     *
     * @param remainingBalance  outstanding principal before last installment
     * @param lastMonthInterest interest due for the last month
     * @return exact final EMI = remainingBalance + lastMonthInterest
     */
    public static BigDecimal adjustFinalEmi(
            BigDecimal remainingBalance,
            BigDecimal lastMonthInterest) {
        return roundHalfUp(remainingBalance.add(lastMonthInterest));
    }

    // ── Guard validations ──────────────────────────────────────────

    /**
     * Throws BusinessRuleException if value is null, zero, or negative.
     * Used in services before passing values into calculations.
     *
     * @param value     the amount to check
     * @param fieldName displayed in the exception message
     */
    public static void validatePositive(
            BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException(
                    fieldName + " must be a positive amount.");
        }
    }

    /**
     * Returns true if value is null, zero, or negative.
     * Used in DTI calculation to short-circuit when income is invalid.
     */
    public static boolean isZeroOrNegative(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
    }
}

