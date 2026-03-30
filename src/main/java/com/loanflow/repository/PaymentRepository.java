package com.loanflow.repository;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.entity.Loan;
import com.loanflow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query(value = "SELECT nextval('payment_receipt_seq')", nativeQuery = true)
    Long getNextReceiptSequence();

    boolean existsByEmiSchedule(EmiSchedule emiSchedule);

    List<Payment> findByLoan_LoanNumberOrderByPaidAtDesc(String loanNumber);

    List<Payment> findByLoanOrderByPaidAtDesc(Loan loan);

    Optional<Payment> findByReceiptNumber(String receiptNumber);
}
