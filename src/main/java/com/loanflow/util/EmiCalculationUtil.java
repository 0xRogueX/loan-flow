package com.loanflow.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pure EMI formula implementations.
 * No Spring beans — pure static math.
 * All three strategies delegate to these methods.
 */
public final class EmiCalculationUtil {

    private EmiCalculationUtil() {}

    // ── Rate conversion ────────────────────────────────────────────

    /**
     * Converts annual interest rate (%) to monthly decimal rate.
     * Formula: annualRate / 100 / 12  = annualRate / 1200
     *
     * Examples:
     *   12% p.a. → 0.01 per month
     *   8.5% p.a. → 0.0070833... per month
     *
     * Precision kept at 10 decimal places to avoid compounding
     * rounding error across many installments.
     *
     * @param annualRatePercent  e.g. BigDecimal("12.00") for 12%
     * @return monthly rate as decimal, 10dp precision
     */
    public static BigDecimal calculateMonthlyRate(
            BigDecimal annualRatePercent) {
        return annualRatePercent
                .divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
    }

    // ── Reducing balance EMI formula ───────────────────────────────

    /**
     * Standard reducing balance (PMT) formula:
     *   EMI = P × r × (1+r)^n / ((1+r)^n - 1)
     *
     * Used by:
     *   ReducingBalanceStrategy — as the fixed EMI for all installments.
     *   StepUpEmiStrategy       — as the BASE EMI before annual 5% step-up.
     *
     * @param principal    approved loan amount
     * @param monthlyRate  output of calculateMonthlyRate()
     * @param tenureMonths total number of installments
     * @return EMI rounded to 2 decimal places
     */
    public static BigDecimal calculateReducingBalanceEmi(
            BigDecimal principal,
            BigDecimal monthlyRate,
            int tenureMonths) {

        // (1 + r)^n
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power    = onePlusR.pow(tenureMonths);

        // P × r × (1+r)^n
        BigDecimal numerator = principal
                .multiply(monthlyRate)
                .multiply(power);

        // (1+r)^n - 1
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return MoneyUtil.roundHalfUp(numerator.divide(denominator, 10, RoundingMode.HALF_UP));
    }

    // ── Flat rate helpers ──────────────────────────────────────────

    /**
     * Monthly principal portion for flat rate loans.
     * Formula: principal / tenureMonths
     *
     * In flat rate, principal is divided equally every month.
     * Used by FlatRateStrategy only.
     *
     * @param principal    approved loan amount
     * @param tenureMonths total installments
     * @return monthly principal component rounded to 2dp
     */
    public static BigDecimal calculateFlatMonthlyPrincipal(
            BigDecimal principal,
            int tenureMonths) {
        return MoneyUtil.roundHalfUp(
                principal.divide(
                        new BigDecimal(tenureMonths),
                        10, RoundingMode.HALF_UP));
    }

    /**
     * Monthly interest portion for flat rate loans.
     * Formula: principal × monthlyRate
     *
     * In flat rate, interest is always on the ORIGINAL principal,
     * not the outstanding balance — this is what makes it "flat".
     * Used by FlatRateStrategy only.
     *
     * @param principal   approved loan amount (never changes)
     * @param monthlyRate output of calculateMonthlyRate()
     * @return monthly interest component rounded to 2dp
     */
    public static BigDecimal calculateFlatMonthlyInterest(
            BigDecimal principal,
            BigDecimal monthlyRate) {
        return MoneyUtil.roundHalfUp(principal.multiply(monthlyRate));
    }
}

