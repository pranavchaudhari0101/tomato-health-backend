package com.tomatohealth.repository;

import com.tomatohealth.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SensorData entity with type-based and time-range queries.
 */
@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findBySensorTypeOrderByRecordedAtDesc(String sensorType);

    Optional<SensorData> findTopBySensorTypeOrderByRecordedAtDesc(String sensorType);

    List<SensorData> findByRecordedAtBetween(LocalDateTime start, LocalDateTime end);

    List<SensorData> findByDeviceId(String deviceId);
}
