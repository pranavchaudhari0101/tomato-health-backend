package com.tomatohealth.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for sensor data readings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataResponse {

    private Long id;
    private String sensorType;
    private Double value;
    private String unit;
    private String deviceId;
    private LocalDateTime recordedAt;
}
