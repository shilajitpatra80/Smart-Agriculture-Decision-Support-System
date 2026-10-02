package com.project.agriculture.repository;

import com.project.agriculture.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    // Used for GET /api/disease/{cropId}
    List<Disease> findByCropId(Long cropId);
}
