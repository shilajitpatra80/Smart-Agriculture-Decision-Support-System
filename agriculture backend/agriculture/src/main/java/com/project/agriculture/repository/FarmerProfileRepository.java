package com.project.agriculture.repository;

import com.project.agriculture.entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {

    // Fetch a farmer's profile using their linked user account id
    Optional<FarmerProfile> findByUserId(Long userId);
}
