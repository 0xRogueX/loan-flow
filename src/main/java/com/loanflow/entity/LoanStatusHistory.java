package com.loanflow.entity;


import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "loan_status_history",
        indexes = @Index(name = "idx_lsh_loan" , columnList = "loan_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id" ,nullable = false)
    private Loan loan;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(length =  500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime changedAt;

}
