package com.testdashboard.seeder;

import com.testdashboard.entity.Project;
import com.testdashboard.entity.TestCase;
import com.testdashboard.entity.TestRun;
import com.testdashboard.enums.Environment;
import com.testdashboard.enums.TestStatus;
import com.testdashboard.repository.ProjectRepository;
import com.testdashboard.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataSeeder implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final TestRunRepository testRunRepository;

    @Override
    public void run(String... args) {
        if (testRunRepository.count() > 0) {
            log.info("Data already seeded — skipping");
            return;
        }

        log.info("Seeding test data — 30 days of history across 3 projects");

        String[] projectNames = {
            "Google Search Automation",
            "Gmail Regression Suite",
            "Google Maps E2E Tests"
        };

        String[] triggers = {"CI/CD Pipeline", "Manual Run", "Nightly Build"};
        String[] branches = {"main", "develop", "release/v2.0"};
        Environment[] envs = Environment.values();

        String[] testNames = {
            "verifySearchResultsLoad",
            "verifyFilterByDateWorks",
            "verifyPaginationWorks",
            "verifyAutoCompleteWorks",
            "verifySearchSuggestionsAppear",
            "verifyImageSearchWorks",
            "verifyNewsTabLoads",
            "verifyMapsIntegration",
            "verifyLoginRedirect",
            "verifyLogoutFlow"
        };

        Random random = new Random();

        for (String name : projectNames) {
            Project project = projectRepository.save(
                    Project.builder()
                            .name(name)
                            .description("Automation suite for " + name)
                            .owner("automation-team@company.com")
                            .isActive(true)
                            .build());

            // 30 days of run history per project
            for (int day = 30; day >= 0; day--) {
                int total   = 80 + random.nextInt(40);
                int failed  = random.nextInt(15);
                int skipped = random.nextInt(5);
                int passed  = total - failed - skipped;

                TestStatus status = failed > 0 ? TestStatus.FAILED : TestStatus.PASSED;

                TestRun run = TestRun.builder()
                        .project(project)
                        .environment(envs[random.nextInt(envs.length)])
                        .triggeredBy(triggers[random.nextInt(triggers.length)])
                        .buildNumber("BUILD-" + (1000 + day))
                        .branch(branches[random.nextInt(branches.length)])
                        .totalTests(total)
                        .passed(passed)
                        .failed(failed)
                        .skipped(skipped)
                        .durationSeconds(180 + random.nextInt(120))
                        .status(status)
                        .createdAt(LocalDateTime.now().minusDays(day))
                        .build();

                // Seed individual test cases for each run
                List<TestCase> cases = new ArrayList<>();
                for (String testName : testNames) {
                    TestStatus caseStatus;
                    int roll = random.nextInt(100);
                    if (roll < 80)       caseStatus = TestStatus.PASSED;
                    else if (roll < 93)  caseStatus = TestStatus.FAILED;
                    else                 caseStatus = TestStatus.SKIPPED;

                    cases.add(TestCase.builder()
                            .testRun(run)
                            .testName(testName)
                            .className("com.automation." + name.replace(" ", ""))
                            .status(caseStatus)
                            .durationSeconds(1 + random.nextInt(8))
                            .errorMessage(caseStatus == TestStatus.FAILED
                                    ? "AssertionError: Expected true but was false" : null)
                            .build());
                }
                run.setTestCases(cases);
                testRunRepository.save(run);
            }
        }

        log.info("Seeding complete — {} projects, 93 runs total",
                projectNames.length);
    }
}