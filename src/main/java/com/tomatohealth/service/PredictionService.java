package com.tomatohealth.service;

import com.tomatohealth.dto.prediction.MLPredictionResponse;
import com.tomatohealth.dto.prediction.PredictionHistoryResponse;
import com.tomatohealth.dto.prediction.PredictionResponse;
import com.tomatohealth.entity.DiseaseInfo;
import com.tomatohealth.entity.Prediction;
import com.tomatohealth.entity.User;
import com.tomatohealth.exception.BadRequestException;
import com.tomatohealth.exception.ResourceNotFoundException;
import com.tomatohealth.exception.UnauthorizedException;
import com.tomatohealth.repository.DiseaseInfoRepository;
import com.tomatohealth.repository.PredictionRepository;
import com.tomatohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for disease detection predictions using the Flask ML service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final DiseaseInfoRepository diseaseInfoRepository;
    private final StorageService storageService;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    /**
     * Detect disease from an uploaded tomato leaf image.
     *
     * @param file     the leaf image file
     * @param username the authenticated user's username
     * @return prediction response with disease details
     */
    @Transactional
    public PredictionResponse detectDisease(MultipartFile file, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Upload image
        String imageUrl = storageService.uploadImage(file);

        // Call Flask ML service
        MLPredictionResponse mlResponse = callMLService(file);

        // Enrich with disease info from database
        String description = mlResponse.getDescription();
        String recommendation = mlResponse.getRecommendation();
        String severity = mlResponse.getSeverity();

        Optional<DiseaseInfo> diseaseInfo = diseaseInfoRepository
                .findByDiseaseNameIgnoreCase(mlResponse.getDisease());

        if (diseaseInfo.isPresent()) {
            DiseaseInfo info = diseaseInfo.get();
            if (description == null || description.isEmpty()) {
                description = info.getDescription();
            }
            if (recommendation == null || recommendation.isEmpty()) {
                recommendation = info.getTreatment();
            }
            if (severity == null || severity.isEmpty()) {
                severity = info.getSeverityLevel();
            }
        }

        // Save prediction
        Prediction prediction = Prediction.builder()
                .user(user)
                .imageUrl(imageUrl)
                .diseaseName(mlResponse.getDisease())
                .confidence(mlResponse.getConfidence())
                .severity(severity)
                .description(description)
                .recommendation(recommendation)
                .predictedAt(LocalDateTime.now())
                .build();

        prediction = predictionRepository.save(prediction);
        log.info("Prediction saved for user {}: disease={}, confidence={}",
                username, mlResponse.getDisease(), mlResponse.getConfidence());

        return mapToResponse(prediction);
    }

    /**
     * Get paginated prediction history for a user.
     *
     * @param username the user's username
     * @param page     page number
     * @param size     page size
     * @param search   optional search term for disease name
     * @param sortBy   sort field
     * @return paginated prediction history
     */
    @Transactional(readOnly = true)
    public PredictionHistoryResponse getHistory(String username, int page, int size,
                                                 String search, String sortBy) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        String sortField = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "predictedAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortField));

        Page<Prediction> predictions;
        if (StringUtils.hasText(search)) {
            predictions = predictionRepository.findByUserIdAndSearch(user.getId(), search, pageable);
        } else {
            predictions = predictionRepository.findByUserId(user.getId(), pageable);
        }

        return PredictionHistoryResponse.builder()
                .content(predictions.getContent().stream().map(this::mapToResponse).toList())
                .pageNumber(predictions.getNumber())
                .pageSize(predictions.getSize())
                .totalElements(predictions.getTotalElements())
                .totalPages(predictions.getTotalPages())
                .last(predictions.isLast())
                .build();
    }

    /**
     * Get a single prediction by ID.
     *
     * @param id the prediction ID
     * @return prediction response
     */
    @Transactional(readOnly = true)
    public PredictionResponse getPredictionById(Long id) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction", "id", id));
        return mapToResponse(prediction);
    }

    /**
     * Delete a prediction by ID, verifying ownership.
     *
     * @param id       the prediction ID
     * @param username the authenticated user's username
     */
    @Transactional
    public void deletePrediction(Long id, String username) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction", "id", id));

        if (!prediction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to delete this prediction");
        }

        predictionRepository.delete(prediction);
        log.info("Prediction {} deleted by user {}", id, username);
    }

    /**
     * Call the Flask ML service to get disease prediction.
     */
    private MLPredictionResponse callMLService(MultipartFile file) {
        try {
            // Configure timeouts for Render free-tier cold starts
            org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                    new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(90000);  // 90 seconds
            factory.setReadTimeout(90000);     // 90 seconds
            RestTemplate restTemplate = new RestTemplate(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Calling ML service at: {}/predict", mlServiceUrl);
            ResponseEntity<MLPredictionResponse> response = restTemplate.exchange(
                    mlServiceUrl + "/predict",
                    HttpMethod.POST,
                    requestEntity,
                    MLPredictionResponse.class
            );

            if (response.getBody() == null) {
                throw new BadRequestException("Empty response from ML service");
            }

            log.info("ML service returned: disease={}, confidence={}",
                    response.getBody().getDisease(), response.getBody().getConfidence());

            return response.getBody();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling ML service: {}", e.getMessage());
            // Return a fallback response when ML service is unavailable
            return MLPredictionResponse.builder()
                    .disease("Unknown")
                    .confidence(0.0)
                    .severity("Unknown")
                    .description("ML service is currently unavailable. Please try again later.")
                    .recommendation("Please ensure the ML service is running and try again.")
                    .build();
        }
    }

    /**
     * Map Prediction entity to PredictionResponse DTO.
     */
    private PredictionResponse mapToResponse(Prediction prediction) {
        PredictionResponse.PredictionResponseBuilder builder = PredictionResponse.builder()
                .id(prediction.getId())
                .imageUrl(prediction.getImageUrl())
                .diseaseName(prediction.getDiseaseName())
                .confidence(prediction.getConfidence())
                .severity(prediction.getSeverity())
                .description(prediction.getDescription())
                .recommendation(prediction.getRecommendation())
                .predictedAt(prediction.getPredictedAt());

        // Fetch full static details from disease_info to enrich the response for tabbed display
        diseaseInfoRepository.findByDiseaseNameIgnoreCase(prediction.getDiseaseName())
                .ifPresent(info -> {
                    builder.symptoms(info.getSymptoms());
                    builder.causes(info.getCauses());
                    builder.prevention(info.getPrevention());
                    builder.treatment(info.getTreatment());
                });

        return builder.build();
    }
}
