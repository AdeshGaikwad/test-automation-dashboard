package com.testdashboard.service;

import com.testdashboard.dto.response.CompareResponse;
import com.testdashboard.dto.response.FailureAnalysisResponse;
import com.testdashboard.dto.response.SummaryResponse;
import com.testdashboard.dto.response.TrendResponse;
import com.testdashboard.dto.response.TestRunResponse;
import com.testdashboard.entity.TestRun;
import com.testdashboard.enums.TestStatus;
import com.testdashboard.exception.ResourceNotFoundException;
import com.testdashboard.repository.TestCaseRepository;
import com.testdashboard.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TestRunRepository testRunRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProjectService projectService;
    private final TestRunService testRunService;

    // @Cacheable — summary is expensive to compute, cache for 5 minutes
    @Cacheable(value = "summary", key = "#projectId")
    public SummaryResponse getSummary(Long projectId) {
        projectService.getById(projectId); // validate project exists

        long totalRuns  = testRunRepository.countByProjectId(projectId);
        long passedRuns = testRunRepository.countByProjectIdAndStatus(
                projectId, TestStatus.PASSED);
        long failedRuns = testRunRepository.countByProjectIdAndStatus(
                projectId, TestStatus.FAILED);
        Double avgPassRate  = testRunRepository.findAveragePassRateByProjectId(projectId);
        Double avgDuration  = testRunRepository.findAverageDurationByProjectId(projectId);

        TestRun lastRun = testRunRepository
                .findTopByProjectIdOrderByCreatedAtDesc(projectId).orElse(null);

        return SummaryResponse.builder()
                .projectName(projectService.getById(projectId).getName())
                .totalRuns(totalRuns)
                .passedRuns(passedRuns)
                .failedRuns(failedRuns)
                .avgPassRate(avgPassRate != null
                        ? Math.round(avgPassRate * 100.0) / 100.0 : 0.0)
                .avgDurationSeconds(avgDuration != null
                        ? Math.round(avgDuration * 100.0) / 100.0 : 0.0)
                .lastRunStatus(lastRun != null ? lastRun.getStatus() : null)
                .lastRunAt(lastRun != null ? lastRun.getCreatedAt() : null)
                .build();
    }

    // Day-by-day pass/fail trend for the last N days
    @Cacheable(value = "trends", key = "#projectId + '_' + #days")
    public List<TrendResponse> getTrends(Long projectId, int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        LocalDateTime to   = LocalDateTime.now();

        List<TestRun> runs = testRunRepository
                .findByProjectIdAndDateRange(projectId, from, to);

        // Group runs by date
        Map<LocalDate, List<TestRun>> byDate = runs.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate()));

        List<TrendResponse> trends = new ArrayList<>();
        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<TestRun> dayRuns = byDate.getOrDefault(date, List.of());

            int passed  = dayRuns.stream().mapToInt(TestRun::getPassed).sum();
            int failed  = dayRuns.stream().mapToInt(TestRun::getFailed).sum();
            int skipped = dayRuns.stream().mapToInt(TestRun::getSkipped).sum();
            int total   = passed + failed + skipped;
            double passRate = total > 0 ? (passed * 100.0 / total) : 0.0;

            trends.add(TrendResponse.builder()
                    .date(date)
                    .totalRuns(dayRuns.size())
                    .passed(passed)
                    .failed(failed)
                    .skipped(skipped)
                    .passRate(Math.round(passRate * 100.0) / 100.0)
                    .build());
        }
        return trends;
    }

    // Most frequently failing test cases for a project
    public List<FailureAnalysisResponse> getFailureAnalysis(Long projectId, int limit) {
        long totalRuns = testRunRepository.countByProjectId(projectId);

        List<Object[]> results = testCaseRepository
                .findMostFrequentlyFailingTests(
                        projectId, PageRequest.of(0, limit));

        return results.stream()
                .map(row -> {
                    String testName  = (String) row[0];
                    Long failCount   = (Long) row[1];
                    double failRate  = totalRuns > 0
                            ? (failCount * 100.0 / totalRuns) : 0.0;

                    return FailureAnalysisResponse.builder()
                            .testName(testName)
                            .failCount(failCount)
                            .failRate(Math.round(failRate * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Side-by-side comparison of two runs
    public CompareResponse compareRuns(Long runIdA, Long runIdB) {
        TestRun runA = testRunRepository.findById(runIdA)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Run not found: " + runIdA));
        TestRun runB = testRunRepository.findById(runIdB)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Run not found: " + runIdB));

        TestRunResponse responseA = testRunService.mapToResponse(runA, false);
        TestRunResponse responseB = testRunService.mapToResponse(runB, false);

        int passedDiff   = runB.getPassed()   - runA.getPassed();
        int failedDiff   = runB.getFailed()   - runA.getFailed();
        int durationDiff = runB.getDurationSeconds() - runA.getDurationSeconds();
        double passRateDiff = responseB.getPassRate() - responseA.getPassRate();

        // Verdict — did the suite improve or regress?
        String verdict;
        if (failedDiff < 0)       verdict = "IMPROVED — fewer failures in run B";
        else if (failedDiff > 0)  verdict = "REGRESSED — more failures in run B";
        else                      verdict = "NO CHANGE";

        return CompareResponse.builder()
                .runA(responseA)
                .runB(responseB)
                .passedDiff(passedDiff)
                .failedDiff(failedDiff)
                .durationDiff(durationDiff)
                .passRateDiff(Math.round(passRateDiff * 100.0) / 100.0)
                .verdict(verdict)
                .build();
    }
}