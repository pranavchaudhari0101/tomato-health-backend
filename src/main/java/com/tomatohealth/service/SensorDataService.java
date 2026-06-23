package com.tomatohealth.service;

import com.tomatohealth.dto.sensor.SensorDataRequest;
import com.tomatohealth.dto.sensor.SensorDataResponse;
import com.tomatohealth.entity.SensorData;
import com.tomatohealth.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing IoT sensor data readings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;

    private static final List<String> SENSOR_TYPES = Arrays.asList(
            "temperature", "humidity", "soil_moisture", "light", "ph"
    );

    /**
     * Save a new sensor data reading.
     *
     * @param request sensor data request
     * @return saved sensor data response
     */
    @Transactional
    public SensorDataResponse saveData(SensorDataRequest request) {
        SensorData sensorData = SensorData.builder()
                .sensorType(request.getSensorType())
                .value(request.getValue())
                .unit(request.getUnit())
                .deviceId(request.getDeviceId())
                .recordedAt(LocalDateTime.now())
                .build();

        sensorData = sensorDataRepository.save(sensorData);
        log.info("Sensor data saved: type={}, value={}{}, device={}",
                request.getSensorType(), request.getValue(), request.getUnit(), request.getDeviceId());

        return mapToResponse(sensorData);
    }

    /**
     * Get the latest sensor reading for each sensor type.
     *
     * @return list of latest sensor data responses
     */
    @Transactional(readOnly = true)
    public List<SensorDataResponse> getLatest() {
        return SENSOR_TYPES.stream()
                .map(type -> sensorDataRepository.findTopBySensorTypeOrderByRecordedAtDesc(type))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get sensor data history filtered by type and date range.
     *
     * @param sensorType optional sensor type filter
     * @param from       optional start date
     * @param to         optional end date
     * @return list of sensor data responses
     */
    @Transactional(readOnly = true)
    public List<SensorDataResponse> getHistory(String sensorType, LocalDateTime from, LocalDateTime to) {
        if (sensorType != null && !sensorType.isEmpty()) {
            return sensorDataRepository.findBySensorTypeOrderByRecordedAtDesc(sensorType)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        if (from != null && to != null) {
            return sensorDataRepository.findByRecordedAtBetween(from, to)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return sensorDataRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map SensorData entity to SensorDataResponse DTO.
     */
    private SensorDataResponse mapToResponse(SensorData data) {
        return SensorDataResponse.builder()
                .id(data.getId())
                .sensorType(data.getSensorType())
                .value(data.getValue())
                .unit(data.getUnit())
                .deviceId(data.getDeviceId())
                .recordedAt(data.getRecordedAt())
                .build();
    }
}
