package com.project.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "weather_data",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_location_date",
        columnNames = {"location", "record_date"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String location;

    // Column is named record_date in the DB because DATE is a reserved word in MySQL
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(precision = 6, scale = 2)
    private BigDecimal rainfall;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
