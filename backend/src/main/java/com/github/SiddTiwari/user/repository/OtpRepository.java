package com.github.SiddTiwari.user.repository;

import com.github.SiddTiwari.user.domain.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByEmailOrderByCreatedAtDesc(String email);
    void deleteAllByEmail(String email);
}
