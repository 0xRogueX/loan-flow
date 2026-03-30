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

@Component
public class FlatRateStrategy implements EmiCalculationStrategy {

    @Override
    public List<EmiSchedule> generateEmiSchedule(Loan loan) {

        List<EmiSchedule> schedule = new ArrayList<>();

        BigDecimal principal = loan.getApprovedAmount();
        BigDecimal monthlyInterestRate = EmiCalculationUtil.calculateMonthlyRate(loan.getInterestRatePerAnnum());
        Integer tenureMonths = loan.getTenureMonths();
        LocalDate disbursedOn = loan.getDisbursedAt().toLocalDate();

        // fix for all installments
        BigDecimal monthlyPrincipal = EmiCalculationUtil.calculateFlatMonthlyPrincipal(principal, tenureMonths);
        BigDecimal monthlyInterest = EmiCalculationUtil.calculateFlatMonthlyInterest(principal, monthlyInterestRate);
        BigDecimal emi = monthlyPrincipal.add(monthlyInterest);

        BigDecimal balance = principal;

        for (int i = 1; i <= tenureMonths; i++) {

            balance = balance.subtract(monthlyPrincipal);

            EmiSchedule entry = EmiSchedule.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(DateUtil.emiDueDate(disbursedOn, i))
                    .principalAmount(monthlyPrincipal)
                    .interestAmount(monthlyInterest)
                    .build();

            if (i == tenureMonths) {
                entry.setTotalEmiAmount(
                        MoneyUtil.adjustFinalEmi(balance.add(monthlyPrincipal), monthlyInterest)
                );
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
