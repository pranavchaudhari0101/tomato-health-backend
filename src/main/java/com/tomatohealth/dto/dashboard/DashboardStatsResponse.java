package com.tomatohealth.dto.dashboard;

import com.tomatohealth.dto.prediction.PredictionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for dashboard statistics overview.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalScans;
    private long healthyCount;
    private long diseasedCount;
    private long todayScans;
    private double averageConfidence;
    private List<PredictionResponse> recentPredictions;
}
