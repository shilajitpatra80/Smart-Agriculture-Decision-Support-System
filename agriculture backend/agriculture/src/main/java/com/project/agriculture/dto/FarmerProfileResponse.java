package com.project.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfileResponse {
    private Long id;
    private Long userId;
    private String name;   // pulled from the linked User, so the frontend gets it in one call
    private String email;
    private String location;
    private BigDecimal landSize;
    private String soilType;
    private BigDecimal waterAvailability;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
