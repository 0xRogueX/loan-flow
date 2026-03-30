package com.loanflow.service.impl;

import com.loanflow.dto.response.LoanPortfolioResponse;
import com.loanflow.dto.response.OverdueSummaryResponse;
import com.loanflow.enums.LoanStatus;
import com.loanflow.enums.PenaltyStatus;
import com.loanflow.repository.LoanRepository;
import com.loanflow.repository.OverdueTrackerRepository;
import com.loanflow.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final LoanRepository loanRepository;
    private final OverdueTrackerRepository overdueTrackerRepository;

    @Override
    @Transactional(readOnly = true)
    public OverdueSummaryResponse getOverdueSummary() {
        long overdueCount = overdueTrackerRepository
                .countByResolvedAtIsNull();
        return OverdueSummaryResponse.builder()
                .totalOverdueCount(overdueCount)
                .totalPenaltyOutstanding(
                        overdueTrackerRepository.sumOutstandingPenalty(PenaltyStatus.UNPAID))
                .oldestOverdueDays(
                        overdueTrackerRepository.findMaxDaysOverdue() != null
                                ? overdueTrackerRepository.findMaxDaysOverdue() : 0)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public LoanPortfolioResponse getPortfolioSummary() {
        return LoanPortfolioResponse.builder()
                .activeCount(
                        loanRepository.countByStatus(LoanStatus.ACTIVE))
                .closedCount(
                        loanRepository.countByStatus(LoanStatus.CLOSED))
                .defaultedCount(
                        loanRepository.countByStatus(LoanStatus.DEFAULTED))
                .writtenOffCount(
                        loanRepository.countByStatus(LoanStatus.WRITTEN_OFF))
                .totalDisbursedAmount(
                        loanRepository.sumAllApprovedAmounts())
                .build();
    }

}
