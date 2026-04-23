package com.testdashboard.service;

import com.testdashboard.dto.response.SummaryResponse;
import com.testdashboard.entity.Project;
import com.testdashboard.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectRepository projectRepository;
    private final AnalyticsService analyticsService;

    // Called by DailyReportScheduler every morning at 9AM
    public void sendDailyReports() {
        List<Project> projects = projectRepository.findByIsActiveTrue();
        log.info("Generating daily reports for {} projects", projects.size());

        projects.forEach(project -> {
            try {
                SummaryResponse summary = analyticsService.getSummary(project.getId());

                // Build the daily report string
                String report = String.format(
                        "Daily Test Report — %s%n" +
                        "Total Runs : %d%n" +
                        "Avg Pass Rate : %.1f%%%n" +
                        "Last Run Status : %s%n" +
                        "Avg Duration : %.0f seconds",
                        project.getName(),
                        summary.getTotalRuns(),
                        summary.getAvgPassRate(),
                        summary.getLastRunStatus(),
                        summary.getAvgDurationSeconds());

                // Log the report — ready to wire up email when SMTP is configured
                // To enable email: inject NotificationService and call:
                // notificationService.sendEmail(project.getOwner(), "Daily Report", report);
                log.info("Daily report for [{}]:\n{}", project.getName(), report);

            } catch (Exception e) {
                log.error("Failed to generate report for project {}: {}",
                        project.getName(), e.getMessage());
            }
        });
    }
}