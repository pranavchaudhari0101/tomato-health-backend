package com.tomatohealth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Prediction entity representing a disease detection result from the ML model.
 */
@Entity
@Table(name = "predictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "disease_name", nullable = false)
    private String diseaseName;

    @Column(nullable = false)
    private Double confidence;

    @Column(length = 50)
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "predicted_at", nullable = false)
    private LocalDateTime predictedAt;

    @PrePersist
    protected void onCreate() {
        if (predictedAt == null) {
            predictedAt = LocalDateTime.now();
        }
    }
}
