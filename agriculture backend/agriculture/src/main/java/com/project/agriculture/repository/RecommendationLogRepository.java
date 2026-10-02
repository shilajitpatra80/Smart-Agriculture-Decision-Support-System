package com.project.agriculture.repository;

import com.project.agriculture.entity.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    // A farmer's recommendation history — useful for the dashboard
    // and, later, as training data if you add a real ML model
    List<RecommendationLog> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
}
