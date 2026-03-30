package com.loanflow.service;

import com.loanflow.dto.response.LoanPortfolioResponse;
import com.loanflow.dto.response.OverdueSummaryResponse;

public interface ReportService {

    OverdueSummaryResponse getOverdueSummary();

    LoanPortfolioResponse getPortfolioSummary();
}
