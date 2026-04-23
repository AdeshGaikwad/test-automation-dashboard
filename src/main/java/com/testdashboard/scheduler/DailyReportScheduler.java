package com.testdashboard.scheduler;

import com.testdashboard.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final ReportService reportService;

    // Runs every day at 9:00 AM
    // cron format: second minute hour day month weekday
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReports() {
        log.info("DailyReportScheduler triggered — sending daily reports");
        reportService.sendDailyReports();
    }
}