package com.testdashboard.dto.response;

import com.testdashboard.enums.Environment;
import com.testdashboard.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestRunResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private Environment environment;
    private String triggeredBy;
    private String buildNumber;
    private String branch;
    private Integer totalTests;
    private Integer passed;
    private Integer failed;
    private Integer skipped;
    private Integer durationSeconds;
    private Double passRate;
    private TestStatus status;
    private List<TestCaseResponse> testCases;
    private LocalDateTime createdAt;
}