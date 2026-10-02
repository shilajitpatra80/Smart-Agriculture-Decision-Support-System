package com.project.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseResponse {
    private Long id;
    private Long cropId;
    private String cropName;
    private String name;
    private String symptoms;
    private String treatment;
    private LocalDateTime createdAt;
}
