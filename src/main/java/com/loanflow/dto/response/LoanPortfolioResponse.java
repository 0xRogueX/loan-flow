package com.loanflow.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanPortfolioResponse {

    private long activeCount;

    private long closedCount;

    private long defaultedCount;

    private long writtenOffCount;

    /** Sum of approvedAmount for all loans (all statuses) */
    private BigDecimal totalDisbursedAmount;
}


