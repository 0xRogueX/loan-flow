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

    /**
     * Entity type being audited.
     * Examples: "LOAN_APPLICATION", "LOAN", "EMI_SCHEDULE", "PAYMENT"
     * Never use @NotBlank on an entity — use @Column(nullable = false)
     * for DB constraint. @NotBlank works only on DTO + @Valid.
     */
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;

    /**
     * UUID of the entity being audited.
     * Matches the id of LoanApplication, Loan, EmiSchedule, etc.
     */
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    /**
     * What happened.
     * Examples: "SUBMITTED", "APPROVED", "REJECTED",
     *           "MARKED_PAID", "MARKED_OVERDUE", "CANCELLED"
     */
    @Column(nullable = false, length = 50)
    private String action;

    /** Status before the transition. Null for creation events. */
    @Column(name = "old_status", length = 30)
    private String oldStatus;

    /** Status after the transition. */
    @Column(name = "new_status", length = 30)
    private String newStatus;

    /**
     * JSON string for complex changes.
     * Example: officer overrides strategy →
     * {"suggestedStrategy":"FLAT_RATE","finalStrategy":"REDUCING_BALANCE"}
     * Null for simple status transitions.
     */
    @Column(name = "change_detail_json", columnDefinition = "TEXT")
    private String changeDetailJson;

    /**
     * The user who performed the action.
     * NULL when actorRole = "SYSTEM" (scheduler, auto-reject, etc.)
     * References the abstract User parent — works for all subtypes.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    /**
     * WHO performed the action — used for filtering audit logs.
     * Values: "BORROWER", "LOAN_OFFICER", "ADMIN", "SYSTEM"
     * Use LoanConstants.ACTOR_* constants — never hardcode strings.
     *
     * This field was MISSING from the original class.
     * Without it you cannot filter "show all system auto-rejections"
     * or "show all officer decisions" in the admin audit log view.
     */
    @Column(name = "actor_role", nullable = false, length = 20)
    private Role actorRole;

    /** Optional human-readable context for this audit entry. */
    @Column(length = 1000)
    private String remarks;

    /**
     * Auto-set at object creation time.
     * AuditLog does NOT extend BaseEntity so this is set here directly.
     * AuditService does NOT need to call setCreatedAt() — this handles it.
     * updatable = false ensures it is never accidentally overwritten.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}