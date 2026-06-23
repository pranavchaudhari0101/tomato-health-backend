package com.tomatohealth.dto.sensor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting sensor data readings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataRequest {

    @NotBlank(message = "Sensor type is required")
    private String sensorType;

    @NotNull(message = "Value is required")
    private Double value;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String deviceId;
}
