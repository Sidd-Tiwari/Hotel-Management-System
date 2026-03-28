package com.github.SiddTiwari.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank @Size(min = 4, max = 10) String otp, @Size(max = 120) String name) {}
