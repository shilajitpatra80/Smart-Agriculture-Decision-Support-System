package com.project.agriculture.controller;

import com.project.agriculture.dto.CropRecommendationRequest;
import com.project.agriculture.dto.CropRecommendationResult;
import com.project.agriculture.dto.RecommendationHistoryResponse;
import com.project.agriculture.entity.FarmerProfile;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.FarmerProfileRepository;
import com.project.agriculture.security.UserPrincipal;
import com.project.agriculture.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final FarmerProfileRepository farmerProfileRepository;

    // POST /api/recommend/crop
    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/crop")
    public ResponseEntity<List<CropRecommendationResult>> recommendCrop(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CropRecommendationRequest request) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(recommendationService.recommendCrops(farmer, request));
    }

    // GET /api/recommend/history
    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/history")
    public ResponseEntity<List<RecommendationHistoryResponse>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(recommendationService.getHistory(farmer));
    }

    private FarmerProfile findFarmerOrThrow(Long userId) {
        return farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No farmer profile found. Complete your profile first: POST /api/farmer/profile"));
    }
}
