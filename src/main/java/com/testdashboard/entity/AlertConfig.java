package com.testdashboard.entity;

import com.testdashboard.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Alert config is per project
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    // Send alert when failure rate exceeds this % — e.g. 20.0 means 20%
    @Column(nullable = false)
    @Builder.Default
    private Double failureThresholdPercent = 20.0;

    // Who to notify
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertType alertType = AlertType.EMAIL;

    // Email address or Slack webhook URL
    @Column(nullable = false)
    private String alertTarget;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEnabled = true;
}