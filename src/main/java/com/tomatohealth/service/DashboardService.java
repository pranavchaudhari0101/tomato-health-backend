package com.tomatohealth.service;

import com.tomatohealth.dto.dashboard.ChartDataResponse;
import com.tomatohealth.dto.dashboard.DashboardStatsResponse;
import com.tomatohealth.dto.prediction.PredictionResponse;
import com.tomatohealth.entity.Prediction;
import com.tomatohealth.entity.User;
import com.tomatohealth.exception.ResourceNotFoundException;
import com.tomatohealth.repository.PredictionRepository;
import com.tomatohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for computing dashboard statistics and chart data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;

    /**
     * Get dashboard statistics for the specified user.
     *
     * @param username the authenticated user's username
     * @return dashboard statistics
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(String username) {
        User user = findUserByUsername(username);
        Long userId = user.getId();

        long totalScans = predictionRepository.countByUserId(userId);
        long healthyCount = predictionRepository.countByUserIdAndDiseaseName(userId, "Healthy");

        // Also count case-insensitive "healthy" variants
        long diseasedCount = totalScans - healthyCount;

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayScans = predictionRepository.countByUserIdAndPredictedAtAfter(userId, todayStart);

        // Calculate average confidence
        List<Prediction> recentPredictions = predictionRepository
                .findTop5ByUserIdOrderByPredictedAtDesc(userId);

        double averageConfidence = 0.0;
        if (!recentPredictions.isEmpty()) {
            averageConfidence = recentPredictions.stream()
                    .filter(p -> p.getConfidence() != null)
                    .mapToDouble(Prediction::getConfidence)
                    .average()
                    .orElse(0.0);
        }

        List<PredictionResponse> recent = recentPredictions.stream()
                .map(this::mapToResponse)
                .toList();

        return DashboardStatsResponse.builder()
                .totalScans(totalScans)
                .healthyCount(healthyCount)
                .diseasedCount(diseasedCount)
                .todayScans(todayScans)
                .averageConfidence(Math.round(averageConfidence * 100.0) / 100.0)
                .recentPredictions(recent)
                .build();
    }

    /**
     * Get chart data for the specified user's dashboard.
     *
     * @param username the authenticated user's username
     * @return chart data with distributions and trends
     */
    @Transactional(readOnly = true)
    public ChartDataResponse getChartData(String username) {
        User user = findUserByUsername(username);
        Long userId = user.getId();

        // Disease distribution
        List<Object[]> diseaseCounts = predictionRepository.countByDiseaseNameForUser(userId);
        Map<String, Long> diseaseDistribution = new LinkedHashMap<>();
        for (Object[] row : diseaseCounts) {
            diseaseDistribution.put((String) row[0], (Long) row[1]);
        }

        // Weekly stats (last 7 days)
        Map<String, Long> weeklyStats = new LinkedHashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            List<Prediction> dayPredictions = predictionRepository
                    .findByUserIdAndPredictedAtBetween(userId, start, end);
            weeklyStats.put(date.format(dayFormatter), (long) dayPredictions.size());
        }

        // Monthly stats (last 12 months)
        Map<String, Long> monthlyStats = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            LocalDateTime start = monthStart.atStartOfDay();
            LocalDateTime end = monthEnd.atTime(LocalTime.MAX);
            List<Prediction> monthPredictions = predictionRepository
                    .findByUserIdAndPredictedAtBetween(userId, start, end);
            monthlyStats.put(monthStart.format(monthFormatter), (long) monthPredictions.size());
        }

        // Prediction trend (last 30 days)
        List<Map<String, Object>> predictionTrend = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            List<Prediction> dayPredictions = predictionRepository
                    .findByUserIdAndPredictedAtBetween(userId, start, end);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(dateFormatter));
            dayData.put("count", dayPredictions.size());
            dayData.put("healthy", dayPredictions.stream()
                    .filter(p -> "Healthy".equalsIgnoreCase(p.getDiseaseName()))
                    .count());
            dayData.put("diseased", dayPredictions.stream()
                    .filter(p -> !"Healthy".equalsIgnoreCase(p.getDiseaseName()))
                    .count());
            predictionTrend.add(dayData);
        }

        return ChartDataResponse.builder()
                .diseaseDistribution(diseaseDistribution)
                .weeklyStats(weeklyStats)
                .monthlyStats(monthlyStats)
                .predictionTrend(predictionTrend)
                .build();
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private PredictionResponse mapToResponse(Prediction prediction) {
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
}
