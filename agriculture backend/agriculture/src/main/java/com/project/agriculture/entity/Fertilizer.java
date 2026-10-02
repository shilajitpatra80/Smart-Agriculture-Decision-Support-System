package com.project.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fertilizer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fertilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "quantity_per_acre", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityPerAcre;

    @Column(name = "stage_of_application", nullable = false, length = 100)
    private String stageOfApplication;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
