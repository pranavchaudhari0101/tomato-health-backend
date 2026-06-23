package com.tomatohealth.dto.admin;

import com.tomatohealth.dto.user.UserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for admin analytics dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsResponse {

    private long totalUsers;
    private long totalPredictions;
    private long totalSensorReadings;
    private String mostCommonDisease;
    private Map<String, Long> diseaseFrequency;
    private List<UserProfileResponse> recentUsers;
}
