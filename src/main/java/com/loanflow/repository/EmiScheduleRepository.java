package com.loanflow.repository;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.enums.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    // Amortization table for a loan
    List<EmiSchedule> findByLoanOrderByInstallmentNumberAsc(Loan loan);

    // Daily overdue scanner
    List<EmiSchedule> findByStatusAndDueDateBefore(EmiStatus status, LocalDate date);

    /** it will cause LazyInitializationException  and  The Performance Death (The N+1 Problem) **/
    // Payment reminder: PENDING EMIs due in exactly N days
    // Called with from = today+3, to = today+3 (same day range)
//    List<EmiSchedule> findByStatusAndDueDateBetween(EmiStatus status, LocalDate from, LocalDate to);

    /**
     * Fetches schedules along with the Loan and Borrower in a single database trip
     * to prevent LazyInitializationExceptions in background event listeners.
     */
    @Query("SELECT e FROM EmiSchedule e " +
            "JOIN FETCH e.loan l " +
            "JOIN FETCH l.borrower " +
            "WHERE e.status = :status AND e.dueDate = :dueDate")
    List<EmiSchedule> findUpcomingEmisWithBorrower(
            @Param("status") EmiStatus status,
            @Param("dueDate") LocalDate dueDate);


    // Used by PaymentService to check if all EMIs are PAID
    Long countByLoanAndStatusNot(Loan loan, EmiStatus status);
}
