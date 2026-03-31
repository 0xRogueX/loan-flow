package com.loanflow.entity;

import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.PenaltyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(
        name = "overdue_tracker",
//        indexes = @Index(columnList = "alert_sent, resolved_at")
        indexes = @Index(columnList = "last_alert_at, resolved_at")

)
@Getter
@Setter
@NoArgsConstructor
public class OverdueTracker extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emi_schedule_id", nullable = false, unique = true)
    private EmiSchedule emiSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @Column(nullable = false)
    private LocalDate dueDate;

    // --- Penalty fields ---
    @Column(precision = 12, scale = 2)
    private BigDecimal fixedPenaltyAmount = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal penaltyRate = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal penaltyCharge = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PenaltyStatus penaltyStatus;

    @Column(nullable = false)
    @Min(0)
    private Integer daysOverdue = 0;

    @Column(nullable = false)
    private Integer alertCount = 0;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    private LocalDateTime lastAlertAt;

    private LocalDateTime resolvedAt;
}