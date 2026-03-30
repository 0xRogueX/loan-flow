package com.loanflow.dto.response;

import com.loanflow.enums.LoanStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanStatusHistoryResponse {

    private UUID id;
    private LoanStatus oldStatus;
    private LoanStatus newStatus;

    /** Null when SYSTEM performed the transition */
    private String changedByName;

    private String reason;

    private LocalDateTime changedAt;
}
