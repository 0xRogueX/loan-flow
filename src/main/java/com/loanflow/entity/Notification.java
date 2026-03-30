package com.loanflow.entity;


import com.loanflow.entity.base.BaseEntity;
import com.loanflow.entity.user.User;
import com.loanflow.enums.NotificationEventType;
import com.loanflow.enums.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notif_status" , columnList = "status"),
                @Index(name= "idx_notif_retry" , columnList = "retry_count")
        }
)
@Getter @Setter @NoArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id" , nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id" )
    private Loan loan;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.QUEUED;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false , columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false) @Min(0) @Max(5)
    private Integer retryCount = 0;

    @Column(length = 500)
    private String failureReason;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

}
