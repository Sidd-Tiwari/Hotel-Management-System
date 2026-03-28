package com.github.SiddTiwari.user.web;

import com.github.SiddTiwari.user.service.AuthService;
import com.github.SiddTiwari.user.web.dto.UpdateRoleRequest;
import com.github.SiddTiwari.user.web.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AuthService authService;

    @GetMapping
    public List<UserResponse> allUsers() {
        return authService.allUsers();
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return authService.updateRole(id, request);
    }
}
