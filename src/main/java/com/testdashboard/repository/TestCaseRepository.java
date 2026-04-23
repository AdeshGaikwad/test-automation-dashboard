package com.testdashboard.repository;

import com.testdashboard.entity.TestCase;
import com.testdashboard.enums.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    // All test cases for a specific run
    List<TestCase> findByTestRunId(Long testRunId);

    // Failed test cases for a specific run
    List<TestCase> findByTestRunIdAndStatus(Long testRunId, TestStatus status);

    // Most frequently failing test cases for a project — failure analysis
    @Query("SELECT tc.testName, COUNT(tc) as failCount " +
           "FROM TestCase tc " +
           "WHERE tc.testRun.project.id = :projectId AND tc.status = 'FAILED' " +
           "GROUP BY tc.testName " +
           "ORDER BY failCount DESC")
    List<Object[]> findMostFrequentlyFailingTests(
            @Param("projectId") Long projectId,
            org.springframework.data.domain.Pageable pageable);

    // Count by status for a specific run
    long countByTestRunIdAndStatus(Long testRunId, TestStatus status);
}