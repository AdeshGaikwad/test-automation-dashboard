package com.testdashboard.service;

import com.testdashboard.dto.request.TestRunRequest;
import com.testdashboard.dto.response.PagedResponse;
import com.testdashboard.dto.response.TestCaseResponse;
import com.testdashboard.dto.response.TestRunResponse;
import com.testdashboard.entity.Project;
import com.testdashboard.entity.TestCase;
import com.testdashboard.entity.TestRun;
import com.testdashboard.enums.TestStatus;
import com.testdashboard.exception.ResourceNotFoundException;
import com.testdashboard.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunService {

    private final TestRunRepository testRunRepository;
    private final ProjectService projectService;
    private final AlertService alertService;

    // Evict summary and trends caches after every new run so next read is fresh
    @Caching(evict = {
        @CacheEvict(value = "summary", allEntries = true),
        @CacheEvict(value = "trends",  allEntries = true)
    })
    @Transactional
    public TestRunResponse ingest(TestRunRequest request) {
        // Find or create project automatically
        Project project = projectService.findOrCreate(request.getProjectName());

        // Derive overall status from failed count
        TestStatus status = request.getFailed() > 0
                ? TestStatus.FAILED : TestStatus.PASSED;

        TestRun run = TestRun.builder()
                .project(project)
                .environment(request.getEnvironment())
                .triggeredBy(request.getTriggeredBy())
                .buildNumber(request.getBuildNumber())
                .branch(request.getBranch())
                .totalTests(request.getTotalTests())
                .passed(request.getPassed())
                .failed(request.getFailed())
                .skipped(request.getSkipped())
                .durationSeconds(request.getDurationSeconds())
                .status(status)
                .build();

        // Map individual test cases if provided
        if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
            List<TestCase> cases = request.getTestCases().stream()
                    .map(tc -> TestCase.builder()
                            .testRun(run)
                            .testName(tc.getTestName())
                            .className(tc.getClassName())
                            .status(tc.getStatus())
                            .durationSeconds(tc.getDurationSeconds())
                            .errorMessage(tc.getErrorMessage())
                            .screenshotUrl(tc.getScreenshotUrl())
                            .build())
                    .collect(Collectors.toList());
            run.setTestCases(cases);
        }

        TestRun saved = testRunRepository.save(run);
        log.info("Test run ingested for project: {} | passed: {} failed: {}",
                project.getName(), request.getPassed(), request.getFailed());

        // Check alert threshold after every run
        alertService.checkAndAlert(project.getId(), request.getFailed(),
                request.getTotalTests());

        return mapToResponse(saved, true);
    }

    public PagedResponse<TestRunResponse> getAllRuns(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                Sort.by("createdAt").descending());
        Page<TestRun> page = testRunRepository.findAllByOrderByCreatedAtDesc(pageable);
        return buildPagedResponse(page);
    }

    public PagedResponse<TestRunResponse> getRunsByProject(
            Long projectId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                Sort.by("createdAt").descending());
        Page<TestRun> page = testRunRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId, pageable);
        return buildPagedResponse(page);
    }

    public TestRunResponse getById(Long id) {
        TestRun run = testRunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test run not found"));
        return mapToResponse(run, true);
    }

    // ─── MAPPERS ─────────────────────────────────────────────────────────────

    public TestRunResponse mapToResponse(TestRun run, boolean includeTestCases) {
        double passRate = run.getTotalTests() > 0
                ? (run.getPassed() * 100.0 / run.getTotalTests()) : 0.0;

        List<TestCaseResponse> cases = null;
        if (includeTestCases && run.getTestCases() != null) {
            cases = run.getTestCases().stream()
                    .map(tc -> TestCaseResponse.builder()
                            .id(tc.getId())
                            .testName(tc.getTestName())
                            .className(tc.getClassName())
                            .status(tc.getStatus())
                            .durationSeconds(tc.getDurationSeconds())
                            .errorMessage(tc.getErrorMessage())
                            .screenshotUrl(tc.getScreenshotUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return TestRunResponse.builder()
                .id(run.getId())
                .projectId(run.getProject().getId())
                .projectName(run.getProject().getName())
                .environment(run.getEnvironment())
                .triggeredBy(run.getTriggeredBy())
                .buildNumber(run.getBuildNumber())
                .branch(run.getBranch())
                .totalTests(run.getTotalTests())
                .passed(run.getPassed())
                .failed(run.getFailed())
                .skipped(run.getSkipped())
                .durationSeconds(run.getDurationSeconds())
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .status(run.getStatus())
                .testCases(cases)
                .createdAt(run.getCreatedAt())
                .build();
    }

    private PagedResponse<TestRunResponse> buildPagedResponse(Page<TestRun> page) {
        return PagedResponse.<TestRunResponse>builder()
                .content(page.getContent().stream()
                        .map(r -> mapToResponse(r, false))
                        .collect(Collectors.toList()))
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}