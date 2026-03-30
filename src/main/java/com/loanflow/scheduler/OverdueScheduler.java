package com.loanflow.scheduler;

import com.loanflow.service.OverdueMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueScheduler {

    private final OverdueMonitorService overdueMonitorService;

    // Runs every day at 01:00 AM — marks missed EMIs as OVERDUE
    @Scheduled(cron = "0 0 1 * * *")
    public void runOverdueScan() {
        log.info("Overdue scan started at {}", LocalDateTime.now());
        overdueMonitorService.scanAndMarkOverdue();
        log.info("Overdue scan completed at {}", LocalDateTime.now());
    }

    // Runs every day at 01:30 AM — transitions DEFAULTED → WRITTEN_OFF
    @Scheduled(cron = "0 30 1 * * *")
    public void runWrittenOffScan() {
        log.info("Written-off scan started at {}", LocalDateTime.now());
        overdueMonitorService.scanAndMarkWrittenOff();
        log.info("Written-off scan completed at {}", LocalDateTime.now());
    }
}
