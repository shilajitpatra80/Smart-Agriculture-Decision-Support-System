package com.project.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationHistoryResponse {
    private Long id;
    private String inputData;       // raw JSON string of what was asked
    private String recommendedCrop; // top result at the time
    private LocalDateTime createdAt;
}
