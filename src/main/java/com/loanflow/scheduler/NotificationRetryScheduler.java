package com.loanflow.scheduler;

import com.loanflow.constants.LoanConstants;
import com.loanflow.entity.Notification;
import com.loanflow.enums.NotificationStatus;
import com.loanflow.repository.NotificationRepository;
import com.loanflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationService    notificationService;

    /** Runs every 30 minutes */
    @Scheduled(cron = "0 0/30 * * * *")
    public void retryFailedNotifications() {
        List<Notification> retryable =
                notificationRepository.findByStatusAndRetryCountLessThan(
                        NotificationStatus.FAILED,
                        LoanConstants.MAX_NOTIFICATION_RETRIES);

        log.info("Retrying {} failed notifications", retryable.size());
        retryable.forEach(notificationService::resend);
    }
}
