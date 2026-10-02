package com.project.agriculture.repository;

import com.project.agriculture.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    // Check the cache before calling the external weather API (see Phase 6)
    Optional<WeatherData> findByLocationAndRecordDate(String location, LocalDate recordDate);
}
