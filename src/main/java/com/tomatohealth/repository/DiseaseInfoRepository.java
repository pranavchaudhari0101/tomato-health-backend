package com.tomatohealth.repository;

import com.tomatohealth.entity.DiseaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for DiseaseInfo entity with name-based lookups.
 */
@Repository
public interface DiseaseInfoRepository extends JpaRepository<DiseaseInfo, Long> {

    Optional<DiseaseInfo> findByDiseaseName(String diseaseName);

    Optional<DiseaseInfo> findByDiseaseNameIgnoreCase(String diseaseName);
}
