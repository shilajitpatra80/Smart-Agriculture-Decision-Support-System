package com.project.agriculture.dto;

import com.project.agriculture.entity.Season;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

// What the Admin sends when creating/updating a crop
@Data
public class CropRequest {

    @NotBlank(message = "Crop name is required")
    private String name;

    @NotNull(message = "Season is required")
    private Season season;

    @NotBlank(message = "Soil type is required")
    private String soilType;

    @NotNull(message = "Minimum water requirement is required")
    @PositiveOrZero(message = "Minimum water must be zero or positive")
    private BigDecimal minWater;

    @NotNull(message = "Maximum water requirement is required")
    @PositiveOrZero(message = "Maximum water must be zero or positive")
    private BigDecimal maxWater;

    private String idealTempRange; // optional, e.g. "20-30°C"
}
