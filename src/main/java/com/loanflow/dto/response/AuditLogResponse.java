package com.loanflow.dto.response;

import com.loanflow.enums.EntityType;
import com.loanflow.enums.Role;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private UUID id;
    private EntityType entityType;
    private UUID entityId;
    private String action;
    private String oldStatus;
    private String newStatus;

    /** Raw JSON diff — frontend pretty-prints */
    private String changeDetailJson;

    /** Null when actorRole = SYSTEM */
    private String performedByName;

    private Role actorRole;
    private String remarks;
    private LocalDateTime createdAt;
}
