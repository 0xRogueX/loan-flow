package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.enums.EmiStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "emi_schedules",
        indexes = {
                @Index(columnList = "loan_id"),
                @Index(columnList = "due_date"),
                @Index(columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer installmentNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalEmiAmount;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingBalance;       // after paying this installment

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmiStatus status = EmiStatus.PENDING;

    private LocalDateTime paidAt;
}