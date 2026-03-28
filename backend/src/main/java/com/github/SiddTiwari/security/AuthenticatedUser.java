package com.github.SiddTiwari.security;

public record AuthenticatedUser(
        Long userId,
        String email,
        String name,
        String role
) {
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
