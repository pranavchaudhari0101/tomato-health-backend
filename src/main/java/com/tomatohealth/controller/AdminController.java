package com.tomatohealth.controller;

import com.tomatohealth.dto.admin.AdminAnalyticsResponse;
import com.tomatohealth.dto.prediction.PredictionResponse;
import com.tomatohealth.dto.sensor.SensorDataResponse;
import com.tomatohealth.dto.user.UserProfileResponse;
import com.tomatohealth.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for admin operations. All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints (ADMIN role required)")
public class AdminController {

    private final AdminService adminService;

    /**
     * Get all registered users.
     *
     * @return list of user profiles
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "List all registered users (admin only)")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Get system-wide analytics.
     *
     * @return admin analytics
     */
    @GetMapping("/analytics")
    @Operation(summary = "Get analytics", description = "Get system-wide analytics (admin only)")
    public ResponseEntity<AdminAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     * @return success message
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID (admin only)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    /**
     * Delete a prediction by ID.
     *
     * @param id the prediction ID
     * @return success message
     */
    @DeleteMapping("/predictions/{id}")
    @Operation(summary = "Delete prediction", description = "Delete a prediction by ID (admin only)")
    public ResponseEntity<Map<String, String>> deletePrediction(@PathVariable Long id) {
        adminService.deletePrediction(id);
        return ResponseEntity.ok(Map.of("message", "Prediction deleted successfully"));
    }

    /**
     * Delete a sensor data record by ID.
     *
     * @param id the sensor data ID
     * @return success message
     */
    @DeleteMapping("/sensors/{id}")
    @Operation(summary = "Delete sensor data", description = "Delete a sensor data record by ID (admin only)")
    public ResponseEntity<Map<String, String>> deleteSensorData(@PathVariable Long id) {
        adminService.deleteSensorData(id);
        return ResponseEntity.ok(Map.of("message", "Sensor data deleted successfully"));
    }

    /**
     * Get all predictions with pagination.
     *
     * @param page page number
     * @param size page size
     * @return paginated predictions
     */
    @GetMapping("/predictions")
    @Operation(summary = "Get all predictions", description = "Get all predictions with pagination (admin only)")
    public ResponseEntity<Page<PredictionResponse>> getAllPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "predictedAt"));
        return ResponseEntity.ok(adminService.getAllPredictions(pageable));
    }

    /**
     * Get all sensor data with pagination.
     *
     * @param page page number
     * @param size page size
     * @return paginated sensor data
     */
    @GetMapping("/sensors")
    @Operation(summary = "Get all sensor data", description = "Get all sensor data with pagination (admin only)")
    public ResponseEntity<Page<SensorDataResponse>> getAllSensorData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recordedAt"));
        return ResponseEntity.ok(adminService.getAllSensorData(pageable));
    }
}
