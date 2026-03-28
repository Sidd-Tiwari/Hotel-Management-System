package com.github.SiddTiwari.user.web.dto;

import com.github.SiddTiwari.user.domain.UserRole;

public record AuthResponse(String token, Long userId, String name, String email, UserRole role) {}
