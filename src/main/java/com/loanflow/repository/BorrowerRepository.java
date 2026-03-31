package com.loanflow.repository;

import com.loanflow.entity.user.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

    Optional<Borrower> findByEmail(String email);

     boolean existsByEmail(String email);

     @Query("""
            SELECT SUM(l.monthlyEmi) FROM Loan l
            WHERE l.borrower.id = :id
            AND l.status = 'ACTIVE'
            """)
     Optional<BigDecimal> sumActiveMonthlyEmi(@Param("id") Long borrowerId);
     // Sum of monthly EMI across all ACTIVE loans for this borrower
     // Used by DtiCalculationService as Internal EMI

}
