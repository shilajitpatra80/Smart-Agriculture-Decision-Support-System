package com.project.agriculture.service;

import com.project.agriculture.dto.FarmerProfileRequest;
import com.project.agriculture.dto.FarmerProfileResponse;
import com.project.agriculture.entity.FarmerProfile;
import com.project.agriculture.entity.User;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.FarmerProfileRepository;
import com.project.agriculture.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerProfileService {

    private final FarmerProfileRepository farmerProfileRepository;
    private final UserRepository userRepository;

    // Creates the profile the first time, updates it on every call after that.
    // This "upsert" pattern means the frontend can call the same endpoint
    // whether the farmer is filling the form for the first time or editing it.
    @Transactional
    public FarmerProfileResponse createOrUpdateProfile(Long userId, FarmerProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    FarmerProfile newProfile = new FarmerProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setLocation(request.getLocation());
        profile.setLandSize(request.getLandSize());
        profile.setSoilType(request.getSoilType());
        profile.setWaterAvailability(request.getWaterAvailability());

        FarmerProfile saved = farmerProfileRepository.save(profile);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public FarmerProfileResponse getMyProfile(Long userId) {
        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No farmer profile found yet. Submit POST /api/farmer/profile to create one."));
        return toResponse(profile);
    }

    // ---- Admin-facing ----

    @Transactional(readOnly = true)
    public List<FarmerProfileResponse> getAllProfiles() {
        return farmerProfileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FarmerProfileResponse getProfileById(Long id) {
        FarmerProfile profile = farmerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found with id: " + id));
        return toResponse(profile);
    }

    // ---- helper ----

    private FarmerProfileResponse toResponse(FarmerProfile profile) {
        return new FarmerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getUser().getEmail(),
                profile.getLocation(),
                profile.getLandSize(),
                profile.getSoilType(),
                profile.getWaterAvailability(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
