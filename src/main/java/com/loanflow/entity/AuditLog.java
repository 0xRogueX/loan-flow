package com.loanflow.entity;

import com.loanflow.entity.user.User;
import com.loanflow.enums.EntityType;
import com.loanflow.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                // Query: "show all audit logs for this loan/application"
                @Index(name = "idx_audit_entity",     columnList = "entity_type, entity_id"),

                // Query: "show all SYSTEM actions" or "show all BORROWER actions"
                @Index(name = "idx_audit_actor_role", columnList = "actor_role"),

                // Query: "show audit logs between date X and date Y"
                @Index(name = "idx_audit_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    /**
     * Long + IDENTITY strategy (not UUID) — intentional.
     * Audit logs are insert-only and never joined by their own id.
     * Sequential Long ids give better index performance on
     * append-only tables compared to random UUIDs.
     * All other entities in the project use UUID — this is the
     * only exception and it is documented here.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "old_status", length = 30)
    private String oldStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(name = "change_detail_json", columnDefinition = "TEXT")
    private String changeDetailJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(name = "actor_role", nullable = false, length = 20)
    private Role actorRole;

    @Column(length = 1000)
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}