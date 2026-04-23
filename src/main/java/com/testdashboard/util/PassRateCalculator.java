package com.testdashboard.util;

import org.springframework.stereotype.Component;

@Component
public class PassRateCalculator {

    // Returns pass rate as a percentage rounded to 2 decimal places
    public double calculate(int passed, int total) {
        if (total == 0) return 0.0;
        double rate = (passed * 100.0) / total;
        return Math.round(rate * 100.0) / 100.0;
    }

    // Returns true if failure rate exceeds the given threshold
    public boolean isThresholdBreached(int failed, int total, double thresholdPercent) {
        if (total == 0) return false;
        double failureRate = (failed * 100.0) / total;
        return failureRate >= thresholdPercent;
    }
}