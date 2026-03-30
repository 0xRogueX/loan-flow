package com.loanflow.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OverdueTrackerResponse {

    private UUID id;

    private UUID loanId;

    private UUID emiScheduleId;

    /** Borrower full name — denormalised for officer convenience */
    private String borrowerName;

    private String borrowerEmail;

    private LocalDate dueDate;

    private Integer daysOverdue;

    private BigDecimal fixedPenaltyAmount;

    /////****** ADDED  ********/////////
    private BigDecimal penaltyRate;

    private BigDecimal penaltyCharge;

    private Boolean penaltySettled;

    private Integer alertCount;

    private LocalDateTime detectedAt;

    private LocalDateTime resolvedAt;
}

