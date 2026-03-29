package com.github.SiddTiwari.user.service;

import com.github.SiddTiwari.config.AppProperties;
import com.github.SiddTiwari.notification.ResendMailService;
import com.github.SiddTiwari.security.JwtService;
import com.github.SiddTiwari.user.domain.AppUser;
import com.github.SiddTiwari.user.domain.OtpCode;
import com.github.SiddTiwari.user.domain.UserRole;
import com.github.SiddTiwari.user.repository.OtpRepository;
import com.github.SiddTiwari.user.repository.UserRepository;
import com.github.SiddTiwari.user.web.dto.AuthResponse;
import com.github.SiddTiwari.user.web.dto.OtpDispatchResponse;
import com.github.SiddTiwari.user.web.dto.OtpRequest;
import com.github.SiddTiwari.user.web.dto.UpdateRoleRequest;
import com.github.SiddTiwari.user.web.dto.UserResponse;
import com.github.SiddTiwari.user.web.dto.VerifyOtpRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final ResendMailService resendMailService;
    private final JwtService jwtService;
    private final AppProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public OtpDispatchResponse requestOtp(OtpRequest request) {
        String email = normalizeEmail(request.email());

        otpRepository.findTopByEmailOrderByCreatedAtDesc(email).ifPresent(existing -> {
            if (existing.getCreatedAt()
                    .plusSeconds(properties.getOtp().getResendCooldownSeconds())
                    .isAfter(OffsetDateTime.now())) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Please wait before requesting another OTP"
                );
            }
        });

        otpRepository.deleteAllByEmail(email);

        String otp = generateOtp(properties.getOtp().getLength());

        otpRepository.save(OtpCode.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(OffsetDateTime.now().plusSeconds(properties.getOtp().getTtlSeconds()))
                .used(false)
                .build());

        resendMailService.sendOtp(email, otp);

        return new OtpDispatchResponse(email, "OTP sent successfully");
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String email = normalizeEmail(request.email());

        OtpCode otpCode = otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not requested"));

        boolean invalid = otpCode.isUsed()
                || otpCode.getExpiresAt().isBefore(OffsetDateTime.now())
                || !passwordEncoder.matches(request.otp(), otpCode.getOtpHash());

        if (invalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
        }

        otpCode.setUsed(true);

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> AppUser.builder()
                        .email(email)
                        .name(resolveName(request.name(), email))
                        .role(resolveRole(email))
                        .verified(true)
                        .build());

        user.setVerified(true);
        user.setName(resolveName(request.name(), user.getName() != null ? user.getName() : email));

        if (user.getRole() == null) {
            user.setRole(resolveRole(email));
        }

        AppUser saved = userRepository.save(user);

        return new AuthResponse(
                jwtService.generateToken(saved),
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    public UserResponse me(String email) {
        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    public List<UserResponse> allUsers() {
        return userRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(AppUser::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    private UserRole resolveRole(String email) {
        String adminEmail = properties.getBootstrap().getAdminEmail();
        if (adminEmail != null && adminEmail.equalsIgnoreCase(email)) {
            return UserRole.ADMIN;
        }
        return UserRole.USER;
    }

    private String resolveName(String provided, String fallback) {
        if (provided != null && !provided.isBlank()) {
            return provided.trim();
        }
        String localPart = fallback.contains("@") ? fallback.substring(0, fallback.indexOf("@")) : fallback;
        return localPart.replace('.', ' ').replace('_', ' ').trim();
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isVerified(),
                user.getCreatedAt()
        );
    }

    private String generateOtp(int length) {
        int upper = (int) Math.pow(10, length);
        int lower = upper / 10;
        return String.valueOf(secureRandom.nextInt(lower, upper));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
