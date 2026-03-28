package com.github.SiddTiwari.user.web.dto;

import com.github.SiddTiwari.user.domain.UserRole;

import java.time.OffsetDateTime;

public record UserResponse(Long id, String name, String email, UserRole role, boolean verified, OffsetDateTime createdAt) {}
