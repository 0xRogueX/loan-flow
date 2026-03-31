package com.loanflow.repository;

import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, Long> {

    List<LoanStatusHistory> findByLoanOrderByChangedAtDesc(Loan loan);
}

