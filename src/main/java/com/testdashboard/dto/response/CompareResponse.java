package com.testdashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompareResponse {
    private TestRunResponse runA;
    private TestRunResponse runB;
    private Integer passedDiff;
    private Integer failedDiff;
    private Integer durationDiff;
    private Double passRateDiff;
    private String verdict;
}