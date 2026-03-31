package com.loanflow.strategy;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;

import java.math.BigDecimal;
import java.util.List;

public interface EmiCalculationStrategy {

    List<EmiSchedule> generateEmiSchedule(Loan loan);

    /**
     * Returns the base monthly EMI for this loan without generating the full schedule.
     * Called by LoanServiceImpl to set monthlyEmi on the Loan before persisting.
     *
     * For Flat Rate:         principal/n + flat interest component
     * For Reducing Balance:  standard PMT formula
     * For Step-Up:           base EMI (year-1) from the PMT formula
     */
    BigDecimal calculateBaseEmi(Loan loan);
}
