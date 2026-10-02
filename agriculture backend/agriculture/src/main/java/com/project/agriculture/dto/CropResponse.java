package com.project.agriculture.dto;

import com.project.agriculture.entity.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// What we send back to the frontend — keeps the entity itself hidden from the API
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropResponse {
    private Long id;
    private String name;
    private Season season;
    private String soilType;
    private BigDecimal minWater;
    private BigDecimal maxWater;
    private String idealTempRange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
