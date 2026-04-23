package com.testdashboard.dto.request;

import com.testdashboard.enums.Environment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TestRunRequest {

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotNull(message = "Environment is required")
    private Environment environment;

    private String triggeredBy;
    private String buildNumber;
    private String branch;

    @NotNull(message = "Total tests count is required")
    @Min(value = 1, message = "Total tests must be at least 1")
    private Integer totalTests;

    @NotNull(message = "Passed count is required")
    @Min(value = 0, message = "Passed count cannot be negative")
    private Integer passed;

    @NotNull(message = "Failed count is required")
    @Min(value = 0, message = "Failed count cannot be negative")
    private Integer failed;

    @NotNull(message = "Skipped count is required")
    @Min(value = 0, message = "Skipped count cannot be negative")
    private Integer skipped;

    @NotNull(message = "Duration is required")
    @Min(value = 0, message = "Duration cannot be negative")
    private Integer durationSeconds;

    // Individual test case results — optional
    private List<TestCaseRequest> testCases;
}