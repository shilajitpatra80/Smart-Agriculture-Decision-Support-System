package com.project.agriculture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmerProfileRequest {

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Land size is required")
    @PositiveOrZero(message = "Land size must be zero or positive")
    private BigDecimal landSize;

    @NotBlank(message = "Soil type is required")
    private String soilType;

    @NotNull(message = "Water availability is required")
    @PositiveOrZero(message = "Water availability must be zero or positive")
    private BigDecimal waterAvailability;
}
