package com.project.agriculture.dto;

import com.project.agriculture.entity.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropRecommendationResult {
    private Long cropId;
    private String cropName;
    private Season season;
    private String soilType;
    private BigDecimal minWater;
    private BigDecimal maxWater;
    private String idealTempRange;
    private int score;                 // 0–100, higher = better fit
    private List<String> matchReasons; // human-readable explanation, shown in the UI
}
