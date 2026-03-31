package com.loanflow.dto.response;

import com.loanflow.enums.LoanStatus;
import com.loanflow.enums.LoanStrategy;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanResponse {

    private Long id;

    private String loanNumber;

    private Long applicationId;

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

