package com.project.agriculture.dto;

import com.project.agriculture.entity.IrrigationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationScheduleResponse {
    private Long id;
    private Long cropId;
    private String cropName;
    private LocalDate scheduleDate;
    private BigDecimal waterAmount;
    private IrrigationStatus status;
}
