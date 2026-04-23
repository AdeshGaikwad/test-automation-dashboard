package com.testdashboard.controller;

import com.testdashboard.dto.request.AlertConfigRequest;
import com.testdashboard.dto.response.ApiResponse;
import com.testdashboard.entity.AlertConfig;
import com.testdashboard.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // Configure alert threshold for a project
    @PostMapping("/config/{projectId}")
    public ResponseEntity<ApiResponse<AlertConfig>> saveConfig(
            @PathVariable Long projectId,
            @Valid @RequestBody AlertConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Alert config saved", alertService.saveConfig(projectId, request)));
    }

    // View current alert config for a project
    @GetMapping("/config/{projectId}")
    public ResponseEntity<ApiResponse<AlertConfig>> getConfig(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getConfig(projectId)));
    }
}