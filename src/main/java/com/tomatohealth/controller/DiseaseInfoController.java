package com.tomatohealth.controller;

import com.tomatohealth.entity.DiseaseInfo;
import com.tomatohealth.service.DiseaseInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for disease reference information.
 */
@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
@Tag(name = "Disease Info", description = "Disease reference information endpoints")
public class DiseaseInfoController {

    private final DiseaseInfoService diseaseInfoService;

    /**
     * Get all disease information records.
     *
     * @return list of all diseases
     */
    @GetMapping
    @Operation(summary = "Get all diseases", description = "Get reference information for all known tomato diseases")
    public ResponseEntity<List<DiseaseInfo>> getAllDiseases() {
        return ResponseEntity.ok(diseaseInfoService.getAllDiseases());
    }

    /**
     * Get disease information by name.
     *
     * @param name the disease name
     * @return disease info
     */
    @GetMapping("/{name}")
    @Operation(summary = "Get disease by name", description = "Get detailed information about a specific disease")
    public ResponseEntity<DiseaseInfo> getDiseaseByName(@PathVariable String name) {
        return ResponseEntity.ok(diseaseInfoService.getDiseaseByName(name));
    }
}
