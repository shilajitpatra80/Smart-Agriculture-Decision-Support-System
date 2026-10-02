package com.project.agriculture.controller;

import com.project.agriculture.dto.DiseaseRequest;
import com.project.agriculture.dto.DiseaseResponse;
import com.project.agriculture.service.DiseaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    // ---------------- Admin management endpoints ----------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/diseases")
    public ResponseEntity<List<DiseaseResponse>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/diseases/{id}")
    public ResponseEntity<DiseaseResponse> getDiseaseById(@PathVariable Long id) {
        return ResponseEntity.ok(diseaseService.getDiseaseById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/diseases")
    public ResponseEntity<DiseaseResponse> createDisease(@Valid @RequestBody DiseaseRequest request) {
        DiseaseResponse created = diseaseService.createDisease(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/admin/diseases/{id}")
    public ResponseEntity<DiseaseResponse> updateDisease(
            @PathVariable Long id,
            @Valid @RequestBody DiseaseRequest request) {
        return ResponseEntity.ok(diseaseService.updateDisease(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/admin/diseases/{id}")
    public ResponseEntity<Void> deleteDisease(@PathVariable Long id) {
        diseaseService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Farmer-facing read-only endpoint ----------------
    // Any logged-in user (FARMER or ADMIN) can look up disease info for a crop.

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @GetMapping("/api/disease/{cropId}")
    public ResponseEntity<List<DiseaseResponse>> getDiseasesForCrop(@PathVariable Long cropId) {
        return ResponseEntity.ok(diseaseService.getDiseasesByCropId(cropId));
    }
}
