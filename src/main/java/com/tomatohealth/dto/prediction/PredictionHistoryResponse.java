package com.tomatohealth.dto.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated response DTO for prediction history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistoryResponse {

    private List<PredictionResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
