package com.testdashboard.entity;

import com.testdashboard.enums.Environment;
import com.testdashboard.enums.TestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_runs", indexes = {
    // Frequently queried by project and date range
    @Index(name = "idx_test_run_project", columnList = "project_id"),
    @Index(name = "idx_test_run_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which project this run belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    // Who or what triggered this run — "CI/CD Pipeline", "Adesh Gaikwad" etc.
    private String triggeredBy;

    @Column(nullable = false)
    private Integer totalTests;

    @Column(nullable = false)
    private Integer passed;

    @Column(nullable = false)
    private Integer failed;

    @Column(nullable = false)
    private Integer skipped;

    // Total time taken for the suite to complete in seconds
    @Column(nullable = false)
    private Integer durationSeconds;

    // Overall run status — derived from failed count
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status;

    // Build number or CI job ID — useful for tracing back to CI system
    private String buildNumber;

    // Git branch this run was triggered on
    private String branch;

    // One run has many individual test case results
    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}