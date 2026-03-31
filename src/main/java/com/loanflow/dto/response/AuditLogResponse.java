package com.loanflow.dto.response;

import com.loanflow.enums.EntityType;
import com.loanflow.enums.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private EntityType entityType;
    private Long entityId;
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
