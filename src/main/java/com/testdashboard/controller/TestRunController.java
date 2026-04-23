package com.testdashboard.controller;

import com.testdashboard.dto.request.TestRunRequest;
import com.testdashboard.dto.response.ApiResponse;
import com.testdashboard.dto.response.PagedResponse;
import com.testdashboard.dto.response.TestRunResponse;
import com.testdashboard.service.TestRunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-runs")
@RequiredArgsConstructor
public class TestRunController {

    private final TestRunService testRunService;

    // Main endpoint — automation frameworks POST results here
    @PostMapping
    public ResponseEntity<ApiResponse<TestRunResponse>> ingest(
            @Valid @RequestBody TestRunRequest request) {
        TestRunResponse response = testRunService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test run recorded successfully", response));
    }

    // All runs across all projects (paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TestRunResponse>>> getAllRuns(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(
                testRunService.getAllRuns(pageNo, pageSize)));
    }

    // Runs for a specific project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedResponse<TestRunResponse>>> getByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(
                testRunService.getRunsByProject(projectId, pageNo, pageSize)));
    }

    // Full details of one run including all test cases
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestRunResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(testRunService.getById(id)));
    }
}