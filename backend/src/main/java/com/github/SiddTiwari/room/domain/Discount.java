package com.github.SiddTiwari.room.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "discounts", indexes = {
        @Index(name = "idx_discounts_code", columnList = "code", unique = true),
        @Index(name = "idx_discounts_active", columnList = "active")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    private String code;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(nullable = false, precision = 10, scale = 2, name = "minimum_booking_amount")
    private BigDecimal minimumBookingAmount;

    @Column(nullable = false, name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, name = "created_at")
    private OffsetDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
