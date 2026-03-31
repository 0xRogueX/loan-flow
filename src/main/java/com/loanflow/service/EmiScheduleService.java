package com.loanflow.service;

import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.entity.Loan;
import com.loanflow.strategy.EmiCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;

public interface EmiScheduleService {

    BigDecimal generateSchedule(Loan loan, EmiCalculationStrategy strategy);

    List<EmiScheduleResponse> getScheduleByLoan(Long loanId);
    List<EmiScheduleResponse> getScheduleByLoanNumber(String loanNumber);
}
