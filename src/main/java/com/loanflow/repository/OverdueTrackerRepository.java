package com.loanflow.repository;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.entity.OverdueTracker;
import com.loanflow.enums.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OverdueTrackerRepository extends JpaRepository<OverdueTracker, Long> {

    Optional<OverdueTracker> findByEmiSchedule(EmiSchedule emiSchedule);

    // All unresolved overdue tracker records across all loans
    List<OverdueTracker> findByResolvedAtIsNull();

    // All unresolved overdue records for a loan
    List<OverdueTracker> findByLoanAndResolvedAtIsNull(Loan loan);

    // Count of currently active overdue EMIs.
    // resolvedAt IS NULL = payment has NOT been made yet.
    //Used by ReportService.getOverdueSummary().
    long countByResolvedAtIsNull();


    // Total outstanding penalty for officer dashboard
    @Query("""
       SELECT COALESCE(SUM(t.fixedPenaltyAmount), 0)
       FROM OverdueTracker t
       WHERE t.penaltyStatus = :status
       """)
    BigDecimal sumOutstandingPenalty(@Param("status") PenaltyStatus status);

    // Longest overdue in days — officer dashboard
    @Query("""
           SELECT MAX(t.daysOverdue)
           FROM OverdueTracker t
           WHERE t.resolvedAt IS NULL
           """)
    Integer findMaxDaysOverdue();

}
