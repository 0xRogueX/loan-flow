package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.EmiStatus;
import com.loanflow.enums.PaymentMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_receipt_number", columnList = "receipt_number", unique = true)
        }
)
@Getter @Setter @NoArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "receipt_number", unique = true, nullable = false, updatable = false, length = 30)
    private String receiptNumber; // e.g., RCP-20260329-1001

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emi_schedule_id", nullable = false, unique = true)
    private EmiSchedule emiSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @NotNull @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMode paymentMode = PaymentMode.SIMULATION;

    @Column(nullable = false)
    private LocalDateTime paidAt;
}