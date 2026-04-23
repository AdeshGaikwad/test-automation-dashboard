package com.testdashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {
    // @EnableScheduling activates @Scheduled on DailyReportScheduler
    // @EnableAsync activates @Async on NotificationService
}