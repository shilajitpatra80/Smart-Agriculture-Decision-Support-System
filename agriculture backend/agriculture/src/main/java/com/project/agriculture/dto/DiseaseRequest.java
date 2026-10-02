package com.project.agriculture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiseaseRequest {

    @NotNull(message = "Crop id is required")
    private Long cropId;

    @NotBlank(message = "Disease name is required")
    private String name;

    @NotBlank(message = "Symptoms are required")
    private String symptoms;

    @NotBlank(message = "Treatment is required")
    private String treatment;
}
