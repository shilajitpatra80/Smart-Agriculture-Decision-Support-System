package com.project.agriculture.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IrrigationScheduleRequest {

    @NotNull(message = "Crop id is required")
    private Long cropId;

    // Optional — defaults to today if not provided
    private LocalDate startDate;

    // Optional — how many days between waterings. Defaults to 4 if not provided.
    @Positive(message = "Interval days must be greater than zero")
    private Integer intervalDays;

    // Optional — total length of the growing period to schedule for. Defaults to 30 if not provided.
    @Positive(message = "Duration days must be greater than zero")
    private Integer durationDays;
}
