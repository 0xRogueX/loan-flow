package com.loanflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyCollectionResponse {

    /** Year of the collection period, e.g. 2026 */
    private int year;

    /** Month of the collection period (1 = January … 12 = December) */
    private int month;

    /** Number of EMI instalments paid in this period */
    private long totalPaymentsCount;

    /** Total amount collected (sum of paidAmount) in this period */
    private BigDecimal totalAmountCollected;

    /** Number of instalments that became overdue in this period */
    private long overdueCount;

    /** Total penalty charged for overdue instalments in this period */
    private BigDecimal totalPenaltyCharged;
}
