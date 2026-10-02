package com.project.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilizerResponse {
    private Long id;
    private Long cropId;
    private String cropName;   // included so the frontend doesn't need a second lookup
    private String name;
    private BigDecimal quantityPerAcre;
    private String stageOfApplication;
    private LocalDateTime createdAt;
}
