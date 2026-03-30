package com.loanflow.strategy;

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
 * REDUCING BALANCE: interest on OUTSTANDING("Kitna loan abhi bhi baki hai") principal each month.
 * EMI = same every month (fixed by formula).
 * Interest = decreasing month by month.
 * Principal = increasing month by month.
 */

@Component
public class ReducingBalanceStrategy implements EmiCalculationStrategy {
    @Override
    public List<EmiSchedule> generateEmiSchedule(Loan loan) {

        List<EmiSchedule> schedule = new ArrayList<>();

        BigDecimal principal = loan.getApprovedAmount();
        BigDecimal monthlyInterestRate = EmiCalculationUtil.calculateMonthlyRate(loan.getInterestRatePerAnnum());
        Integer tenureMonths = loan.getTenureMonths();
        LocalDate disbursedOn = loan.getDisbursedAt().toLocalDate();

        // fix emi for all installments
        BigDecimal emi = EmiCalculationUtil.calculateReducingBalanceEmi(principal, monthlyInterestRate, tenureMonths);

        BigDecimal balance = principal;

        for (int i = 1; i <= tenureMonths; i++) {

            // interest of this month = outstanding balance * monthlyInterestRate
            BigDecimal interest = MoneyUtil.roundHalfUp(balance.multiply(monthlyInterestRate));

            // principal of this month = EMI - interest
            BigDecimal principalPart = MoneyUtil.roundHalfUp(emi.subtract(interest));
            balance = balance.subtract(principalPart);

            EmiSchedule entry = EmiSchedule.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(DateUtil.emiDueDate(disbursedOn, i))
                    .principalAmount(principalPart)
                    .interestAmount(interest)
                    .build();

            if (i == tenureMonths) {
                BigDecimal lastEmi = MoneyUtil.adjustFinalEmi(principalPart.add(balance), interest);
                entry.setTotalEmiAmount(lastEmi);
                entry.setRemainingBalance(BigDecimal.ZERO);
            } else {
                entry.setTotalEmiAmount(MoneyUtil.roundHalfUp(emi));
                entry.setRemainingBalance(MoneyUtil.roundHalfUp(balance));
            }
            schedule.add(entry);
        }
        return schedule;
    }
}
