package com.project.agriculture.repository;

import com.project.agriculture.entity.Fertilizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FertilizerRepository extends JpaRepository<Fertilizer, Long> {

    // Used for GET /api/fertilizer/{cropId}
    List<Fertilizer> findByCropId(Long cropId);
}
