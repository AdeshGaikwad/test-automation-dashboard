package com.testdashboard.dto.response;

import com.testdashboard.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryResponse {
    private String projectName;
    private Long totalRuns;
    private Long passedRuns;
    private Long failedRuns;
    private Double avgPassRate;
    private Double avgDurationSeconds;
    private TestStatus lastRunStatus;
    private LocalDateTime lastRunAt;
}