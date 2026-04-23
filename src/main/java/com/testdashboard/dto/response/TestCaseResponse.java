package com.testdashboard.dto.response;

import com.testdashboard.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResponse {
    private Long id;
    private String testName;
    private String className;
    private TestStatus status;
    private Integer durationSeconds;
    private String errorMessage;
    private String screenshotUrl;
}