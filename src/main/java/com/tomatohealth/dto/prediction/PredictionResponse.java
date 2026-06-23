package com.tomatohealth.dto.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for a single prediction result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {

    private Long id;
    private String imageUrl;
    private String diseaseName;
    private Double confidence;
    private String severity;
    private String description;
    private String recommendation;
    private String symptoms;
    private String causes;
    private String prevention;
    private String treatment;
    private LocalDateTime predictedAt;
}
