package com.project.agriculture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FertilizerRequest {

    @NotNull(message = "Crop id is required")
    private Long cropId;

    @NotBlank(message = "Fertilizer name is required")
    private String name;

    @NotNull(message = "Quantity per acre is required")
    @Positive(message = "Quantity per acre must be greater than zero")
    private BigDecimal quantityPerAcre;

    @NotBlank(message = "Stage of application is required")
    private String stageOfApplication;
}
