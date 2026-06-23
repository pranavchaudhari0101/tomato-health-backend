package com.tomatohealth.service;

import com.tomatohealth.dto.admin.AdminAnalyticsResponse;
import com.tomatohealth.dto.prediction.PredictionResponse;
import com.tomatohealth.dto.sensor.SensorDataResponse;
import com.tomatohealth.dto.user.UserProfileResponse;
import com.tomatohealth.entity.Prediction;
import com.tomatohealth.entity.SensorData;
import com.tomatohealth.entity.User;
import com.tomatohealth.exception.ResourceNotFoundException;
import com.tomatohealth.repository.PredictionRepository;
import com.tomatohealth.repository.SensorDataRepository;
import com.tomatohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for admin-level operations including user management and system analytics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;
    private final SensorDataRepository sensorDataRepository;

    /**
     * Get all registered users.
     *
     * @return list of user profiles
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserProfile)
                .collect(Collectors.toList());
    }

    /**
     * Get system-wide analytics for the admin dashboard.
     *
     * @return admin analytics response
     */
    @Transactional(readOnly = true)
    public AdminAnalyticsResponse getAnalytics() {
        long totalUsers = userRepository.count();
        long totalPredictions = predictionRepository.count();
        long totalSensorReadings = sensorDataRepository.count();

        // Disease frequency
        List<Object[]> diseaseCounts = predictionRepository.countByDiseaseName();
        Map<String, Long> diseaseFrequency = new LinkedHashMap<>();
        String mostCommonDisease = "N/A";
        long maxCount = 0;

        for (Object[] row : diseaseCounts) {
            String disease = (String) row[0];
            Long count = (Long) row[1];
            diseaseFrequency.put(disease, count);
            if (count > maxCount) {
                maxCount = count;
                mostCommonDisease = disease;
            }
        }

        // Recent users (last 10)
        List<UserProfileResponse> recentUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(this::mapToUserProfile)
                .collect(Collectors.toList());

        return AdminAnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .totalPredictions(totalPredictions)
                .totalSensorReadings(totalSensorReadings)
                .mostCommonDisease(mostCommonDisease)
                .diseaseFrequency(diseaseFrequency)
                .recentUsers(recentUsers)
                .build();
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
        log.info("Admin deleted user: {} (ID: {})", user.getUsername(), id);
    }

    /**
     * Delete a prediction by ID (admin).
     *
     * @param id the prediction ID
     */
    @Transactional
    public void deletePrediction(Long id) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction", "id", id));
        predictionRepository.delete(prediction);
        log.info("Admin deleted prediction ID: {}", id);
    }

    /**
     * Delete a sensor data record by ID (admin).
     *
     * @param id the sensor data ID
     */
    @Transactional
    public void deleteSensorData(Long id) {
        SensorData sensorData = sensorDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SensorData", "id", id));
        sensorDataRepository.delete(sensorData);
        log.info("Admin deleted sensor data ID: {}", id);
    }

    /**
     * Get all predictions with pagination (admin).
     *
     * @param pageable pagination info
     * @return page of prediction responses
     */
    @Transactional(readOnly = true)
    public Page<PredictionResponse> getAllPredictions(Pageable pageable) {
        return predictionRepository.findAll(pageable)
                .map(this::mapToPredictionResponse);
    }

    /**
     * Get all sensor data with pagination (admin).
     *
     * @param pageable pagination info
     * @return page of sensor data responses
     */
    @Transactional(readOnly = true)
    public Page<SensorDataResponse> getAllSensorData(Pageable pageable) {
        return sensorDataRepository.findAll(pageable)
                .map(this::mapToSensorResponse);
    }

    private UserProfileResponse mapToUserProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private PredictionResponse mapToPredictionResponse(Prediction prediction) {
        return PredictionResponse.builder()
                .id(prediction.getId())
                .imageUrl(prediction.getImageUrl())
                .diseaseName(prediction.getDiseaseName())
                .confidence(prediction.getConfidence())
                .severity(prediction.getSeverity())
                .description(prediction.getDescription())
                .recommendation(prediction.getRecommendation())
                .predictedAt(prediction.getPredictedAt())
                .build();
    }

    private SensorDataResponse mapToSensorResponse(SensorData data) {
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
