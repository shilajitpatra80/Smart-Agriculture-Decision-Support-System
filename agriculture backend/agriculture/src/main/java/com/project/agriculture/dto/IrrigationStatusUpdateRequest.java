package com.project.agriculture.dto;

import com.project.agriculture.entity.IrrigationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IrrigationStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private IrrigationStatus status; // PENDING, COMPLETED, or SKIPPED
}
