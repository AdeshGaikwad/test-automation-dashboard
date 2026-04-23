package com.testdashboard.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlertCheckScheduler {

    // Runs every hour — logs a heartbeat to confirm scheduler is alive
    // In production: could scan for stale projects with no runs in 24h
    @Scheduled(fixedRate = 3600000)
    public void checkAlerts() {
        log.info("AlertCheckScheduler heartbeat — scheduler is running");
    }
}