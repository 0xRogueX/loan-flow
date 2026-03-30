package com.loanflow.service;

import com.loanflow.entity.Loan;
import com.loanflow.entity.Notification;
import com.loanflow.entity.user.User;
import com.loanflow.enums.NotificationEventType;

import java.util.Map;

public interface NotificationService {

    /**
     * Core method — called by NotificationEventListener for every event.
     * Renders the Thymeleaf template, persists the Notification record,
     * and sends the email.
     */
    void send(
            User recipient,
            Loan loan,
            NotificationEventType eventType,
            String subject,
            String templateName,
            Map<String, Object> model);

    /**
     * Called by NotificationRetryScheduler every 30 minutes.
     * Re-sends using stored HTML content — does NOT re-render Thymeleaf.
     */
    void resend(Notification notification);
}
