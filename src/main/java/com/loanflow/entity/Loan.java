package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.LoanStatus;
import com.loanflow.enums.LoanStrategy;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "loans",
        indexes = {
                @Index(name = "idx_loan_number", columnList = "loan_number"),
                @Index(name = "idx_loan_borrower", columnList = "borrower_id"),
                @Index(name = "idx_loan_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Loan extends BaseEntity {

    @Column(name = "loan_number", nullable = false, unique = true, length = 30)
    private String loanNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private LoanApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "100.0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRatePerAnnum;

    @NotNull
    @Min(1)
    @Max(360)
    @Column(nullable = false)
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStrategy strategy;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyEmi;

    @NotNull
    @PositiveOrZero // Balance can be 0, but never negative
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal outstandingPrincipal;

    @NotNull
    @Min(0) @Max(360)
    @Column(nullable = false)
    private Integer overDueCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    private LocalDateTime disbursedAt;

    private LocalDateTime closedAt;
}