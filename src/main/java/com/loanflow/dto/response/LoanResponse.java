package com.loanflow.dto.response;

import com.loanflow.enums.LoanStatus;
import com.loanflow.enums.LoanStrategy;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanResponse {

    private UUID id;

    private String loanNumber;

    private UUID applicationId;

    private BigDecimal approvedAmount;

    private BigDecimal interestRatePerAnnum;

    private Integer tenureMonths;

    private LoanStrategy strategy;

    /** First-month EMI — actual amount varies per installment for Step-Up */
    private BigDecimal monthlyEmi;

    private BigDecimal outstandingPrincipal;

    private Integer overdueCount;

    private LoanStatus status;

    private LocalDateTime disbursedAt;

    private LocalDateTime closedAt;

    private String approvedByName;
}

