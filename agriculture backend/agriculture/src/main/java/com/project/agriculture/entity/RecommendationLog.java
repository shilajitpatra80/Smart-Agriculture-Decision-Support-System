package com.project.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // References farmer_profile.id (not users.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;

    // Stored as a JSON string, e.g. {"soilType":"Loamy","season":"RABI","availableWater":80}
    // Beginner-friendly approach: keep it a String field and serialize/deserialize
    // with Jackson's ObjectMapper in your service layer. The DB column is native JSON,
    // which still validates it's well-formed JSON even though Hibernate treats it as text.
    @Lob
    @Column(name = "input_data", nullable = false, columnDefinition = "JSON")
    private String inputData;

    @Column(name = "recommended_crop", nullable = false, length = 100)
    private String recommendedCrop;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
