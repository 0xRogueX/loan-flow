package com.loanflow.strategy;

import com.loanflow.constants.LoanConstants;
import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.util.DateUtil;
import com.loanflow.util.EmiCalculationUtil;
import com.loanflow.util.MoneyUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * STEP-UP EMI: EMI increases by 5% every year
 *
 * Year 1: BASE_EMI * (1 + 0.05) ^ 0 = BASE_EMI (using reducing balance formula)
 * Year 2: BASE_EMI * (1 + 0.05) ^ 1
 * Year 3: BASE_EMI * (1 + 0.05) ^ 2
 *
 * BASE_EMI = calculated using reducing balance formula
 */
@Component
public class StepUpEmiStrategy implements EmiCalculationStrategy {

    @Override
    public List<EmiSchedule> generateEmiSchedule(Loan loan) {

        List<EmiSchedule> schedule = new ArrayList<>();

        BigDecimal principal = loan.getApprovedAmount();
        BigDecimal monthlyInterestRate = EmiCalculationUtil.calculateMonthlyRate(loan.getInterestRatePerAnnum());
        Integer tenureMonths = loan.getTenureMonths();
        LocalDate disbursedOn = loan.getDisbursedAt().toLocalDate();

        BigDecimal baseEmi = EmiCalculationUtil.calculateReducingBalanceEmi(principal, monthlyInterestRate, tenureMonths);
        BigDecimal balance = principal;

        for (int i = 1; i <= tenureMonths; i++) {

            // months 1-12 = year 0, months 13-24 = year 1, etc.
            int yearIndex = (i - 1) / 12;

            // emi for this month  = baseEmi * (1 + 0.05) ^ yearIndex
            BigDecimal currentEmi = MoneyUtil.roundHalfUp(
                    baseEmi.multiply(LoanConstants.STEP_UP_ANNUAL_RATE.pow(yearIndex)));
            BigDecimal interest = MoneyUtil.roundHalfUp(
                    balance.multiply(monthlyInterestRate));
            BigDecimal principalPart = MoneyUtil.roundHalfUp(
                    currentEmi.subtract(interest));
            balance = balance.subtract(principalPart);

            EmiSchedule entry = EmiSchedule.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(DateUtil.emiDueDate(disbursedOn, i))
                    .principalAmount(principalPart)
                    .interestAmount(interest)
                    .build();

            if (i == tenureMonths) {
                BigDecimal lastEmi = MoneyUtil.adjustFinalEmi(principalPart.add(balance), interest); // for preventing 0.01 or 0.02 balance due to rounding in previous months
                entry.setTotalEmiAmount(lastEmi);
                entry.setRemainingBalance(BigDecimal.ZERO);
            } else {
                entry.setTotalEmiAmount(currentEmi);
                entry.setRemainingBalance(MoneyUtil.roundHalfUp(balance));
            }
            schedule.add(entry);
        }
        return schedule;
    }
}
