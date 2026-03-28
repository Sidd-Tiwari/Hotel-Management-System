package com.github.SiddTiwari.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "otps", indexes = {
        @Index(name = "idx_otps_email", columnList = "email"),
        @Index(name = "idx_otps_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 255, name = "otp_hash")
    private String otpHash;

    @Column(nullable = false, name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false, name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
