package com.tomatohealth.controller;

import com.tomatohealth.dto.prediction.PredictionHistoryResponse;
import com.tomatohealth.dto.prediction.PredictionResponse;
import com.tomatohealth.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST controller for disease detection predictions.
 */
@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
@Tag(name = "Predictions", description = "Disease detection and prediction history endpoints")
public class PredictionController {

    private final PredictionService predictionService;

    /**
     * Detect disease from an uploaded tomato leaf image.
     *
     * @param file the leaf image file
     * @return prediction result
     */
    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Detect disease", description = "Upload a tomato leaf image for disease detection")
    public ResponseEntity<PredictionResponse> detectDisease(@RequestParam("file") MultipartFile file) {
        String username = getCurrentUsername();
        PredictionResponse response = predictionService.detectDisease(file, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get paginated prediction history for the current user.
     *
     * @param page   page number (default 0)
     * @param size   page size (default 10)
     * @param search optional search term
     * @param sortBy sort field (default predictedAt)
     * @return paginated prediction history
     */
    @GetMapping("/history")
    @Operation(summary = "Get prediction history", description = "Get paginated prediction history for the current user")
    public ResponseEntity<PredictionHistoryResponse> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        String username = getCurrentUsername();
        PredictionHistoryResponse response = predictionService.getHistory(username, page, size, search, sortBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific prediction by ID.
     *
     * @param id the prediction ID
     * @return prediction details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get prediction by ID", description = "Get a specific prediction result")
    public ResponseEntity<PredictionResponse> getPrediction(@PathVariable Long id) {
        return ResponseEntity.ok(predictionService.getPredictionById(id));
    }

    /**
     * Delete a prediction by ID.
     *
     * @param id the prediction ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prediction", description = "Delete a prediction by ID")
    public ResponseEntity<Map<String, String>> deletePrediction(@PathVariable Long id) {
        String username = getCurrentUsername();
        predictionService.deletePrediction(id, username);
        return ResponseEntity.ok(Map.of("message", "Prediction deleted successfully"));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
