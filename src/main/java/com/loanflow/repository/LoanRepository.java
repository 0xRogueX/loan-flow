package com.loanflow.repository;

import com.loanflow.entity.Loan;
import com.loanflow.entity.user.User;
import com.loanflow.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {

    @Query(value = "SELECT nextval('loan_number_seq')", nativeQuery = true)
    Long getNextLoanSequence();

    Optional<Loan> findByLoanNumber(String loanNumber);

    // ReportService: count per status for portfolio summary
    long countByStatus(LoanStatus status);

    // ReportService: count per status for portfolio summary
    @Query("SELECT COALESCE(SUM(l.approvedAmount), 0) FROM Loan l")
    BigDecimal sumAllApprovedAmounts();


    Long countByBorrowerAndStatus(User Browser, LoanStatus status);

    List<Loan> findByBorrowerAndStatus(User Borrower, LoanStatus status);

    List<Loan> findByBorrowerOrderByCreatedAtDesc(User Borrower);

    // Used by OverdueMonitorService.scanAndMarkWrittenOff()
    List<Loan> findByStatusAndUpdatedAtBefore(LoanStatus status, LocalDateTime cutoff);

    @Query("""
            SELECT SUM(l.monthlyEmi) FROM Loan l
            WHERE l.borrower.id = :borrowerId
            AND l.status = 'ACTIVE'
            """)
    Optional<BigDecimal> sumActiveMonthlyEmi(@Param("borrowerId") UUID borrowerId);
     // Sum of monthly EMI across all ACTIVE loans for this borrower
     // Used by DtiCalculationService as Internal EMI
}
