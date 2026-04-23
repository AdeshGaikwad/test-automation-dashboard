package com.testdashboard.dto.request;

import com.testdashboard.enums.AlertType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertConfigRequest {

    @NotNull(message = "Failure threshold is required")
    @Min(value = 1, message = "Threshold must be at least 1%")
    @Max(value = 100, message = "Threshold cannot exceed 100%")
    private Double failureThresholdPercent;

    @NotNull(message = "Alert type is required")
    private AlertType alertType;

    @NotBlank(message = "Alert target is required")
    private String alertTarget;

    private Boolean isEnabled = true;
}