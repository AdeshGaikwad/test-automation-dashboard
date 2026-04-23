package com.testdashboard.repository;

import com.testdashboard.entity.TestRun;
import com.testdashboard.enums.Environment;
import com.testdashboard.enums.TestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {

    // Paginated list of runs for a project
    Page<TestRun> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

    // Filter by project + environment
    Page<TestRun> findByProjectIdAndEnvironmentOrderByCreatedAtDesc(
            Long projectId, Environment environment, Pageable pageable);

    // Filter by project + status
    Page<TestRun> findByProjectIdAndStatusOrderByCreatedAtDesc(
            Long projectId, TestStatus status, Pageable pageable);

    // Most recent run for a project
    Optional<TestRun> findTopByProjectIdOrderByCreatedAtDesc(Long projectId);

    // Runs within a date range — used for trend calculation
    @Query("SELECT t FROM TestRun t WHERE t.project.id = :projectId " +
           "AND t.createdAt >= :from AND t.createdAt <= :to " +
           "ORDER BY t.createdAt ASC")
    List<TestRun> findByProjectIdAndDateRange(
            @Param("projectId") Long projectId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Count runs by status for a project
    long countByProjectIdAndStatus(Long projectId, TestStatus status);

    // Total runs for a project
    long countByProjectId(Long projectId);

    // Average pass rate across all runs for a project
    @Query("SELECT AVG(CAST(t.passed AS double) / t.totalTests * 100) " +
           "FROM TestRun t WHERE t.project.id = :projectId")
    Double findAveragePassRateByProjectId(@Param("projectId") Long projectId);

    // Average duration across all runs for a project
    @Query("SELECT AVG(t.durationSeconds) FROM TestRun t WHERE t.project.id = :projectId")
    Double findAverageDurationByProjectId(@Param("projectId") Long projectId);

    // All runs across all projects — for admin overview
    Page<TestRun> findAllByOrderByCreatedAtDesc(Pageable pageable);
}