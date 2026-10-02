package com.project.agriculture.repository;

import com.project.agriculture.entity.Crop;
import com.project.agriculture.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {

    // Core lookup used by the recommendation engine (Phase 5)
    List<Crop> findBySoilTypeAndSeason(String soilType, Season season);

    // Handy for filtering by season alone (e.g. "what's growable right now")
    List<Crop> findBySeason(Season season);

    // Case-insensitive search, useful for an Admin search box
    List<Crop> findByNameContainingIgnoreCase(String name);
}
