package com.testdashboard.dto.request;

import com.testdashboard.enums.TestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseRequest {

    @NotBlank(message = "Test name is required")
    private String testName;

    private String className;

    @NotNull(message = "Status is required")
    private TestStatus status;

    private Integer durationSeconds;
    private String errorMessage;
    private String screenshotUrl;
}