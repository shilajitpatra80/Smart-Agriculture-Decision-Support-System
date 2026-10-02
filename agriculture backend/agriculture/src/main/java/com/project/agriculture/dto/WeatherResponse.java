package com.project.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String location;
    private LocalDate date;
    private BigDecimal temperature; // °C
    private BigDecimal rainfall;    // mm, last hour (0 if not raining)
    private BigDecimal humidity;    // %
    private String source;          // "LIVE" (just fetched) or "CACHED" (already had today's data)
}
