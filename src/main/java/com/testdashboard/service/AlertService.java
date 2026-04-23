package com.testdashboard.service;

import com.testdashboard.dto.request.AlertConfigRequest;
import com.testdashboard.entity.AlertConfig;
import com.testdashboard.entity.Project;
import com.testdashboard.exception.ResourceNotFoundException;
import com.testdashboard.repository.AlertConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertConfigRepository alertConfigRepository;
    private final ProjectService projectService;
    private final NotificationService notificationService;

    // Called after every test run ingestion
    public void checkAndAlert(Long projectId, int failed, int total) {
        if (total == 0) return;

        alertConfigRepository.findByProjectId(projectId).ifPresent(config -> {
            if (!config.getIsEnabled()) return;

            double failureRate = (failed * 100.0) / total;

            if (failureRate >= config.getFailureThresholdPercent()) {
                log.warn("Alert triggered for projectId: {} — failure rate: {}%",
                        projectId, String.format("%.1f", failureRate));

                String message = String.format(
                        "Alert: Failure rate %.1f%% exceeded threshold %.1f%% " +
                        "for project [%s]",
                        failureRate,
                        config.getFailureThresholdPercent(),
                        config.getProject().getName());

                switch (config.getAlertType()) {
                    case EMAIL -> notificationService.sendEmail(
                            config.getAlertTarget(),
                            "Test Failure Alert — " + config.getProject().getName(),
                            message);
                    case SLACK -> notificationService.sendSlackAlert(
                            config.getAlertTarget(), message);
                }
            }
        });
    }

    @Transactional
    public AlertConfig saveConfig(Long projectId, AlertConfigRequest request) {
        Project project = projectService.getById(projectId);

        AlertConfig config = alertConfigRepository
                .findByProjectId(projectId)
                .orElse(AlertConfig.builder().project(project).build());

        config.setFailureThresholdPercent(request.getFailureThresholdPercent());
        config.setAlertType(request.getAlertType());
        config.setAlertTarget(request.getAlertTarget());
        config.setIsEnabled(request.getIsEnabled());

        return alertConfigRepository.save(config);
    }

    public AlertConfig getConfig(Long projectId) {
        return alertConfigRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No alert config found for this project"));
    }
}