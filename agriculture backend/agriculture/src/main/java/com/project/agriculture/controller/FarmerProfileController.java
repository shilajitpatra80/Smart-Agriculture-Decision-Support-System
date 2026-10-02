package com.project.agriculture.controller;

import com.project.agriculture.dto.FarmerProfileRequest;
import com.project.agriculture.dto.FarmerProfileResponse;
import com.project.agriculture.security.UserPrincipal;
import com.project.agriculture.service.FarmerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FarmerProfileController {

    private final FarmerProfileService farmerProfileService;

    // ---------------- Farmer self-service ----------------
    // @AuthenticationPrincipal injects the UserPrincipal that JwtAuthFilter
    // attached to the request — this is how we know WHICH farmer is calling,
    // without them needing to pass their own id in the URL.

    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/api/farmer/profile")
    public ResponseEntity<FarmerProfileResponse> createOrUpdateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FarmerProfileRequest request) {

        FarmerProfileResponse response = farmerProfileService.createOrUpdateProfile(
                principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/api/farmer/profile")
    public ResponseEntity<FarmerProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(farmerProfileService.getMyProfile(principal.getUser().getId()));
    }

    // ---------------- Admin viewing ----------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/farmers")
    public ResponseEntity<List<FarmerProfileResponse>> getAllProfiles() {
        return ResponseEntity.ok(farmerProfileService.getAllProfiles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/farmers/{id}")
    public ResponseEntity<FarmerProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerProfileService.getProfileById(id));
    }
}
