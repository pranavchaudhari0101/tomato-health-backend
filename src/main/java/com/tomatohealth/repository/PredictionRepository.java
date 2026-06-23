package com.tomatohealth.repository;

import com.tomatohealth.entity.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Prediction entity with analytics and filtering queries.
 */
@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Page<Prediction> findByUserIdOrderByPredictedAtDesc(Long userId, Pageable pageable);

    List<Prediction> findByUserIdAndDiseaseNameContainingIgnoreCase(Long userId, String diseaseName);

    long countByUserId(Long userId);

    long countByUserIdAndDiseaseName(Long userId, String diseaseName);

    long countByUserIdAndPredictedAtAfter(Long userId, LocalDateTime after);

    List<Prediction> findByUserIdAndPredictedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT p.diseaseName, COUNT(p) FROM Prediction p GROUP BY p.diseaseName")
    List<Object[]> countByDiseaseName();

    @Query("SELECT p.diseaseName, COUNT(p) FROM Prediction p WHERE p.user.id = :userId GROUP BY p.diseaseName")
    List<Object[]> countByDiseaseNameForUser(@Param("userId") Long userId);

    Page<Prediction> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT p FROM Prediction p WHERE p.user.id = :userId AND " +
           "(LOWER(p.diseaseName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Prediction> findByUserIdAndSearch(@Param("userId") Long userId,
                                           @Param("search") String search,
                                           Pageable pageable);

    List<Prediction> findTop5ByUserIdOrderByPredictedAtDesc(Long userId);
}
