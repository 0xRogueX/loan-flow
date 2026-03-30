package com.loanflow.scheduler;

import com.loanflow.constants.LoanConstants;
import com.loanflow.entity.EmiSchedule;
import com.loanflow.enums.EmiStatus;
import com.loanflow.event.PaymentReminderEvent;
import com.loanflow.repository.EmiScheduleRepository;
import com.loanflow.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentReminderScheduler {

    private final EmiScheduleRepository emiScheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    /** Runs every day at 09:00 AM server time */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true) // Good practice for background read jobs
    public void runPaymentReminders() {

        LocalDate reminderDate = DateUtil.daysFromToday(LoanConstants.PAYMENT_REMINDER_DAYS_BEFORE);

        // Uses the JOIN FETCH query to safely load Borrower data
        List<EmiSchedule> upcoming = emiScheduleRepository
                .findUpcomingEmisWithBorrower(EmiStatus.PENDING, reminderDate);


//        List<EmiSchedule> upcoming =
//                emiScheduleRepository.findByStatusAndDueDateBetween(
//                        EmiStatus.PENDING, reminderDate, reminderDate);

        log.info("Payment reminder scheduler executed: {} EMIs due on {}", upcoming.size(), reminderDate);

        upcoming.forEach(emi ->
                eventPublisher.publishEvent(new PaymentReminderEvent(emi))
        );
    }
}