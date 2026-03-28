package com.github.SiddTiwari.user.web.dto;

import com.github.SiddTiwari.user.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull UserRole role) {}
