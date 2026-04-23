package com.testdashboard.controller;

import com.testdashboard.dto.response.*;
import com.testdashboard.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // Summary metrics for a project
    @GetMapping("/summary/{projectId}")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getSummary(projectId)));
    }

    // Day-by-day trend for last N days (default 30)
    @GetMapping("/trends/{projectId}")
    public ResponseEntity<ApiResponse<List<TrendResponse>>> getTrends(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getTrends(projectId, days)));
    }

    // Most frequently failing test cases
    @GetMapping("/failures/{projectId}")
    public ResponseEntity<ApiResponse<List<FailureAnalysisResponse>>> getFailures(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getFailureAnalysis(projectId, limit)));
    }

    // Side-by-side comparison of two runs
    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<CompareResponse>> compare(
            @RequestParam Long runIdA,
            @RequestParam Long runIdB) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.compareRuns(runIdA, runIdB)));
    }
}