package com.testdashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailureAnalysisResponse {
    private String testName;
    private Long failCount;
    private Double failRate;
}