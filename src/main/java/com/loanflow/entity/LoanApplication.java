package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.ApplicationStatus;
import com.loanflow.enums.BureauStatus;
import com.loanflow.enums.LoanStrategy;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "loan_applications" ,
        indexes = {
                @Index(name = "idx_loan_application_number", columnList = "application_number"),
                @Index(name = "idx_app_borrower", columnList = "borrower_id"),
                @Index(name = "idx_app_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication extends BaseEntity {

    @Column(name = "application_number", unique = true, nullable = false, updatable = false, length = 20)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;               // null until officer takes action

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @NotNull @Min(1) @Max(360)
    @Column(nullable = false)
    private Integer tenureMonths;

    @NotNull @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Column
    private BureauStatus bureauStatus;

    @NotNull @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal existingMonthlyEmi;

    @Column(precision = 5, scale = 2)
    private BigDecimal calculatedDti;

    @Enumerated(EnumType.STRING)
    private LoanStrategy suggestedStrategy;

    @Enumerated(EnumType.STRING)
    private LoanStrategy finalStrategy;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;

    private LocalDateTime reviewedAt;
}