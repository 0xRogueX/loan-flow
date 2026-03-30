package com.loanflow.service;

import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.entity.Loan;
import com.loanflow.strategy.EmiCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EmiScheduleService {

    BigDecimal generateSchedule(Loan loan, EmiCalculationStrategy strategy);

    List<EmiScheduleResponse> getScheduleByLoan(UUID loanId);
    List<EmiScheduleResponse> getScheduleByLoanNumber(String loanNumber);
}
