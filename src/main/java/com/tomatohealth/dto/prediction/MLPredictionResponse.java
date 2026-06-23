package com.tomatohealth.dto.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO mapping the Flask ML service prediction response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLPredictionResponse {

    private String disease;
    private Double confidence;
    private String severity;
    private String recommendation;
    private String description;
}
