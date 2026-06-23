package com.tomatohealth.controller;

import com.tomatohealth.dto.sensor.SensorDataRequest;
import com.tomatohealth.dto.sensor.SensorDataResponse;
import com.tomatohealth.service.SensorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for IoT sensor data management.
 */
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Tag(name = "Sensor Data", description = "IoT sensor data endpoints")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    /**
     * Submit a new sensor data reading (public endpoint for IoT devices).
     *
     * @param request sensor data
     * @return saved sensor data
     */
    @PostMapping("/data")
    @Operation(summary = "Submit sensor data", description = "Submit a new sensor reading (public endpoint for IoT devices)")
    public ResponseEntity<SensorDataResponse> submitData(@Valid @RequestBody SensorDataRequest request) {
        SensorDataResponse response = sensorDataService.saveData(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get the latest sensor readings for each sensor type.
     *
     * @return list of latest readings
     */
    @GetMapping("/latest")
    @Operation(summary = "Get latest sensor data", description = "Get the most recent reading for each sensor type")
    public ResponseEntity<List<SensorDataResponse>> getLatest() {
        return ResponseEntity.ok(sensorDataService.getLatest());
    }

    /**
     * Get sensor data history with optional filters.
     *
     * @param sensorType optional sensor type filter
     * @param from       optional start date
     * @param to         optional end date
     * @return list of sensor data
     */
    @GetMapping("/history")
    @Operation(summary = "Get sensor history", description = "Get sensor data history with optional type and date filters")
    public ResponseEntity<List<SensorDataResponse>> getHistory(
            @RequestParam(required = false) String sensorType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(sensorDataService.getHistory(sensorType, from, to));
    }
}
