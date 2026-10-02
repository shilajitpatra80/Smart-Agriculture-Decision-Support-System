package com.project.agriculture.controller;

import com.project.agriculture.dto.FertilizerRequest;
import com.project.agriculture.dto.FertilizerResponse;
import com.project.agriculture.service.FertilizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FertilizerController {

    private final FertilizerService fertilizerService;

    // ---------------- Admin management endpoints ----------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/fertilizers")
    public ResponseEntity<List<FertilizerResponse>> getAllFertilizers() {
        return ResponseEntity.ok(fertilizerService.getAllFertilizers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/fertilizers/{id}")
    public ResponseEntity<FertilizerResponse> getFertilizerById(@PathVariable Long id) {
        return ResponseEntity.ok(fertilizerService.getFertilizerById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/fertilizers")
    public ResponseEntity<FertilizerResponse> createFertilizer(@Valid @RequestBody FertilizerRequest request) {
        FertilizerResponse created = fertilizerService.createFertilizer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/admin/fertilizers/{id}")
    public ResponseEntity<FertilizerResponse> updateFertilizer(
            @PathVariable Long id,
            @Valid @RequestBody FertilizerRequest request) {
        return ResponseEntity.ok(fertilizerService.updateFertilizer(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/admin/fertilizers/{id}")
    public ResponseEntity<Void> deleteFertilizer(@PathVariable Long id) {
        fertilizerService.deleteFertilizer(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Farmer-facing read-only endpoint ----------------
    // Any logged-in user (FARMER or ADMIN) can look up fertilizer info for a crop.

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @GetMapping("/api/fertilizer/{cropId}")
    public ResponseEntity<List<FertilizerResponse>> getFertilizersForCrop(@PathVariable Long cropId) {
        return ResponseEntity.ok(fertilizerService.getFertilizersByCropId(cropId));
    }
}
