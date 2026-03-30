package com.loanflow.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OverdueSummaryResponse {

    private long totalOverdueCount;

    /** Sum of penaltyAmount for all unresolved OverdueTracker records */
    private BigDecimal totalPenaltyOutstanding;

    /** Max daysOverdue across all active overdue records */
    private int oldestOverdueDays;
}
