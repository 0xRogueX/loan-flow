package com.loanflow.service;

import com.loanflow.enums.LoanStrategy;

import java.math.BigDecimal;

public interface DtiCalculationService {
    BigDecimal calculateInitialDti(
            BigDecimal internalEmi,
            BigDecimal externalEmi,
            BigDecimal monthlyIncome);

    BigDecimal calculateFinalDti(
            BigDecimal internalEmi,
            BigDecimal externalEmi,
            BigDecimal newLoanEmi,
            BigDecimal monthlyIncome);

    LoanStrategy suggestStrategy(BigDecimal dtiInitial, int tenureMonths);

    void validateIncome(BigDecimal income);
}
