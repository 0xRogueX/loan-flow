package com.loanflow.service.impl;

import com.loanflow.dto.response.EmiScheduleResponse;
import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.mapper.EmiScheduleMapper;
import com.loanflow.repository.EmiScheduleRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.service.EmiScheduleService;
import com.loanflow.strategy.EmiCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmiScheduleServiceImpl implements EmiScheduleService {

    private final EmiScheduleRepository emiScheduleRepository;
    private final LoanRepository loanRepository;
    private final EmiScheduleMapper emiScheduleMapper;

    /**
     * Generates the full amortization schedule for a loan using the
     * given strategy, bulk-saves all installments, and returns the
     * first installment's totalEmiAmount as the base EMI.
     *
     * Called by LoanServiceImpl.processDecision() after loan creation.
     * The returned baseEmi is stored on Loan.monthlyEmi.
     */
    @Override
    @Transactional
    public BigDecimal generateSchedule(Loan loan, EmiCalculationStrategy strategy) {

        // Strategy generates the full schedule (FlatRate / Reducing / StepUp)
        List<EmiSchedule> schedule = strategy.generateEmiSchedule(loan);

        // Bulk insert — one SQL statement instead of N individual inserts
        emiScheduleRepository.saveAll(schedule);

        log.info("Generated {} EMI installments for loan {}",
                schedule.size(), loan.getId());

        // Return installment 1 amount — this is stored as Loan.monthlyEmi
        // For StepUp: this is the BASE EMI (year 1), not the stepped-up amounts
        // For Flat/Reducing: all months have the same EMI, so installment 1 is fine
        return schedule.get(0).getTotalEmiAmount();
    }

    /**
     * Returns the complete amortization table for a loan,
     * ordered by installment number ascending (1 → N).
     * Used by BorrowerController and OfficerController.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getScheduleByLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found: " + loanId));

        List<EmiSchedule> schedule =
                emiScheduleRepository.findByLoanOrderByInstallmentNumberAsc(loan);

        return emiScheduleMapper.toResponseList(schedule);
    }

    @Override
    public List<EmiScheduleResponse> getScheduleByLoanNumber(String loanNumber) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found: " + loanNumber));
        List<EmiSchedule> schedule =
                emiScheduleRepository.findByLoanOrderByInstallmentNumberAsc(loan);
        return emiScheduleMapper.toResponseList(schedule);
    }
}
