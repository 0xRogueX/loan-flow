package com.loanflow.service.impl;

import com.loanflow.entity.Loan;
import com.loanflow.entity.Notification;
import com.loanflow.entity.user.User;
import com.loanflow.enums.NotificationEventType;
import com.loanflow.enums.NotificationStatus;
import com.loanflow.repository.NotificationRepository;
import com.loanflow.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    /**
     * Core send method. Called from NotificationEventListener (@Async).
     * Saves the Notification entity first so even a mail failure is auditable.
     */
    @Override
    public void send(
            User recipient,
            Loan loan,
            NotificationEventType eventType,
            String subject,
            String templateName,
            Map<String, Object> model) {

        // Render HTML from Thymeleaf template
        Context ctx = new Context();
        model.forEach(ctx::setVariable);
        String htmlBody = templateEngine.process(
                "email/" + templateName, ctx);

        // Persist notification record
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setLoan(loan);
        notification.setDestination(recipient.getEmail());
        notification.setEventType(eventType);
        notification.setStatus(NotificationStatus.QUEUED);
        notification.setSubject(subject);
        notification.setContent(htmlBody);
        notification.setScheduledAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(notification);

        // Attempt to send
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipient.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);

            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());
            log.info("Email sent to {} for event {}",
                    recipient.getEmail(), eventType);

        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
            saved.setFailureReason(e.getMessage());
            log.error("Email failed for {}: {}",
                    recipient.getEmail(), e.getMessage());
        }
        notificationRepository.save(saved);
    }

    /** Called by NotificationRetryScheduler for FAILED notifications */
    @Override
    public void resend(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(notification.getDestination());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), true);
            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setFailureReason(e.getMessage());
        }
        notificationRepository.save(notification);
    }
}

