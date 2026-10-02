package com.project.agriculture.dto;

import com.project.agriculture.entity.Season;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CropRecommendationRequest {

    @NotNull(message = "Season is required")
    private Season season;

    // Both optional: if omitted, the engine falls back to the farmer's saved
    // profile values. This lets a farmer quickly ask "what if I had more
    // water?" without editing their whole profile every time.
    private String soilType;

    @PositiveOrZero(message = "Available water must be zero or positive")
    private BigDecimal availableWater;
}
