package com.loanflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    /**
     * Configures a custom Thread Pool for background tasks.
     * This ensures that if the Notification Scheduler is busy sending emails,
     * the Payment Reminder Scheduler can still run simultaneously on a different thread.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // Allows up to 5 jobs to run at the exact same time
        scheduler.setThreadNamePrefix("loanflow-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}