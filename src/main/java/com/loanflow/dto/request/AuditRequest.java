package com.loanflow.dto.request;

import com.loanflow.entity.user.User;
import com.loanflow.enums.EntityType;
import com.loanflow.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuditRequest {

    private final EntityType entityType;   // "LOAN_APPLICATION", "LOAN", "EMI_SCHEDULE"
    private final UUID entityId;
    private final String action;       // "SUBMITTED", "APPROVED", "MARKED_PAID"
    private final String oldStatus;    // null for creation events
    private final String newStatus;
    private final User performedBy;  // null when SYSTEM acts
    private final Role actorRole;    // LoanConstants.ACTOR_* constants
    private final String remarks;      // optional context
}
