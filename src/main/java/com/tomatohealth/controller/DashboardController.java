package com.tomatohealth.controller;

import com.tomatohealth.dto.dashboard.ChartDataResponse;
import com.tomatohealth.dto.dashboard.DashboardStatsResponse;
import com.tomatohealth.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dashboard statistics and chart data.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics and chart data endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get dashboard statistics for the current user.
     *
     * @return dashboard stats
     */
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard stats", description = "Get scan counts, health metrics, and recent predictions")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(dashboardService.getStats(username));
    }

    /**
     * Get chart data for the current user's dashboard.
     *
     * @return chart data
     */
    @GetMapping("/charts")
    @Operation(summary = "Get chart data", description = "Get disease distribution, weekly/monthly stats, and prediction trends")
    public ResponseEntity<ChartDataResponse> getChartData() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(dashboardService.getChartData(username));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
