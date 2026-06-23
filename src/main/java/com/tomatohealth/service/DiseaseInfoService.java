package com.tomatohealth.service;

import com.tomatohealth.entity.DiseaseInfo;
import com.tomatohealth.exception.ResourceNotFoundException;
import com.tomatohealth.repository.DiseaseInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing disease reference information.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiseaseInfoService {

    private final DiseaseInfoRepository diseaseInfoRepository;

    /**
     * Get all disease information records.
     *
     * @return list of all diseases
     */
    @Transactional(readOnly = true)
    public List<DiseaseInfo> getAllDiseases() {
        return diseaseInfoRepository.findAll();
    }

    /**
     * Get disease information by name (case-insensitive).
     *
     * @param name the disease name
     * @return the disease info entity
     */
    @Transactional(readOnly = true)
    public DiseaseInfo getDiseaseByName(String name) {
        return diseaseInfoRepository.findByDiseaseNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Disease", "name", name));
    }
}
