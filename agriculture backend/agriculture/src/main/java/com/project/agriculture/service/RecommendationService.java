package com.project.agriculture.service;

import com.project.agriculture.dto.CropRecommendationRequest;
import com.project.agriculture.dto.CropRecommendationResult;
import com.project.agriculture.dto.RecommendationHistoryResponse;
import com.project.agriculture.entity.Crop;
import com.project.agriculture.entity.FarmerProfile;
import com.project.agriculture.entity.RecommendationLog;
import com.project.agriculture.entity.Season;
import com.project.agriculture.repository.CropRepository;
import com.project.agriculture.repository.RecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);
    private static final int MAX_RESULTS = 5;

    // Scoring weights — tweak these to change how the engine prioritizes factors.
    // They add up to 100, so "score" in the response is a rough 0-100 fit percentage.
    private static final int SOIL_MATCH_POINTS = 40;
    private static final int SEASON_MATCH_POINTS = 30;
    private static final int SEASON_ALL_SEASON_POINTS = 15;
    private static final int WATER_MATCH_POINTS = 30;

    private final CropRepository cropRepository;
    private final RecommendationLogRepository recommendationLogRepository;

    // Spring Boot 4 auto-configures a JsonMapper bean (the Jackson 3 replacement
    // for the old ObjectMapper) — no manual @Bean needed, just inject it.
    private final JsonMapper jsonMapper;

    @Transactional
    public List<CropRecommendationResult> recommendCrops(FarmerProfile farmer, CropRecommendationRequest request) {

        // Fall back to the farmer's saved profile if the request didn't override these
        String soilType = request.getSoilType() != null ? request.getSoilType() : farmer.getSoilType();
        BigDecimal availableWater = request.getAvailableWater() != null
                ? request.getAvailableWater() : farmer.getWaterAvailability();
        Season season = request.getSeason();

        if (soilType == null || availableWater == null) {
            throw new IllegalStateException(
                    "Soil type and available water are required. Provide them in the request, " +
                            "or complete your farmer profile first (POST /api/farmer/profile).");
        }

        // Candidate pool: crops matching this season, plus ALL_SEASON crops
        List<Crop> candidates = cropRepository.findAll().stream()
                .filter(c -> c.getSeason() == season || c.getSeason() == Season.ALL_SEASON)
                .toList();

        List<CropRecommendationResult> results = candidates.stream()
                .map(crop -> scoreCrop(crop, soilType, availableWater, season))
                .sorted(Comparator.comparingInt(CropRecommendationResult::getScore).reversed())
                .limit(MAX_RESULTS)
                .toList();

        logRecommendation(farmer, soilType, season, availableWater, results);

        return results;
    }

    // ---- the actual scoring logic ----

    private CropRecommendationResult scoreCrop(Crop crop, String soilType, BigDecimal availableWater, Season requestedSeason) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // 1. Soil match
        if (crop.getSoilType().equalsIgnoreCase(soilType)) {
            score += SOIL_MATCH_POINTS;
            reasons.add("Soil type matches (" + crop.getSoilType() + ")");
        } else {
            reasons.add("Soil type differs — this crop prefers " + crop.getSoilType());
        }

        // 2. Season match
        if (crop.getSeason() == requestedSeason) {
            score += SEASON_MATCH_POINTS;
            reasons.add("Perfect season match (" + requestedSeason + ")");
        } else if (crop.getSeason() == Season.ALL_SEASON) {
            score += SEASON_ALL_SEASON_POINTS;
            reasons.add("This crop can grow in any season");
        }

        // 3. Water fit — full points inside the ideal range, tapering off the further outside it you are
        double min = crop.getMinWater().doubleValue();
        double max = crop.getMaxWater().doubleValue();
        double available = availableWater.doubleValue();

        if (available >= min && available <= max) {
            score += WATER_MATCH_POINTS;
            reasons.add(String.format("Water availability (%.1f) fits the ideal range (%.1f–%.1f)", available, min, max));
        } else {
            double distance = available < min ? (min - available) : (available - max);
            double range = Math.max(max - min, 1.0); // avoid divide-by-zero
            double closeness = Math.max(0, 1 - (distance / range));
            int partialPoints = (int) Math.round(closeness * WATER_MATCH_POINTS);
            score += partialPoints;
            reasons.add(String.format("Water availability (%.1f) is outside the ideal range (%.1f–%.1f)", available, min, max));
        }

        return new CropRecommendationResult(
                crop.getId(),
                crop.getName(),
                crop.getSeason(),
                crop.getSoilType(),
                crop.getMinWater(),
                crop.getMaxWater(),
                crop.getIdealTempRange(),
                score,
                reasons
        );
    }

    // ---- logging every recommendation made (this is the "collected data" part) ----

    private void logRecommendation(FarmerProfile farmer, String soilType, Season season,
                                   BigDecimal availableWater, List<CropRecommendationResult> results) {
        try {
            Map<String, Object> inputMap = new LinkedHashMap<>();
            inputMap.put("soilType", soilType);
            inputMap.put("season", season);
            inputMap.put("availableWater", availableWater);

            String inputJson = jsonMapper.writeValueAsString(inputMap);
            String topCrop = results.isEmpty() ? "No suitable crop found" : results.get(0).getCropName();

            RecommendationLog entry = new RecommendationLog();
            entry.setFarmer(farmer);
            entry.setInputData(inputJson);
            entry.setRecommendedCrop(topCrop);

            recommendationLogRepository.save(entry);
        } catch (JacksonException e) {
            // Jackson 3 exceptions are unchecked, but we still catch this deliberately:
            // a logging failure shouldn't break the recommendation response itself —
            // just record it and move on.
            log.error("Failed to serialize recommendation input for logging", e);
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendationHistoryResponse> getHistory(FarmerProfile farmer) {
        return recommendationLogRepository.findByFarmerIdOrderByCreatedAtDesc(farmer.getId())
                .stream()
                .map(entry -> new RecommendationHistoryResponse(
                        entry.getId(),
                        entry.getInputData(),
                        entry.getRecommendedCrop(),
                        entry.getCreatedAt()
                ))
                .toList();
    }
}
