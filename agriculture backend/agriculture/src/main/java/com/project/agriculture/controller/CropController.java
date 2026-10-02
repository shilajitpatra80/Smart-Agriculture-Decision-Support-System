package com.project.agriculture.controller;

import com.project.agriculture.dto.CropRequest;
import com.project.agriculture.dto.CropResponse;
import com.project.agriculture.service.CropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/crops")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // every endpoint in this controller requires an ADMIN-role JWT
public class CropController {

    private final CropService cropService;

    // GET /api/admin/crops
    @GetMapping
    public ResponseEntity<List<CropResponse>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    // GET /api/admin/crops/5
    @GetMapping("/{id}")
    public ResponseEntity<CropResponse> getCropById(@PathVariable Long id) {
        return ResponseEntity.ok(cropService.getCropById(id));
    }

    // POST /api/admin/crops
    @PostMapping
    public ResponseEntity<CropResponse> createCrop(@Valid @RequestBody CropRequest request) {
        CropResponse created = cropService.createCrop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/admin/crops/5
    @PutMapping("/{id}")
    public ResponseEntity<CropResponse> updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request) {
        return ResponseEntity.ok(cropService.updateCrop(id, request));
    }

    // DELETE /api/admin/crops/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
