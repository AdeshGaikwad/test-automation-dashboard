package com.testdashboard.entity;

import com.testdashboard.enums.TestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_cases", indexes = {
    @Index(name = "idx_test_case_run", columnList = "test_run_id"),
    @Index(name = "idx_test_case_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many test cases → one test run
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_run_id", nullable = false)
    private TestRun testRun;

    // Full test method name — e.g. "verifySearchResultsLoad"
    @Column(nullable = false)
    private String testName;

    // Class containing this test — e.g. "GoogleSearchTest"
    private String className;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status;

    // Time taken for this individual test in seconds
    private Integer durationSeconds;

    // Stack trace or assertion error — only populated for FAILED tests
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // Screenshot path or URL — for UI automation failures
    private String screenshotUrl;
}