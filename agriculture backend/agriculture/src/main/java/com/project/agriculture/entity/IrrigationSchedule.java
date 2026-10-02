package com.project.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigation_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // References farmer_profile.id (not users.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    // Column is named schedule_date in the DB because DATE is a reserved word in MySQL
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "water_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal waterAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IrrigationStatus status = IrrigationStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
