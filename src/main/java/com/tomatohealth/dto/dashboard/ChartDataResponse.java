package com.tomatohealth.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for dashboard chart data including distributions and trends.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {

    private Map<String, Long> diseaseDistribution;
    private Map<String, Long> weeklyStats;
    private Map<String, Long> monthlyStats;
    private List<Map<String, Object>> predictionTrend;
}
