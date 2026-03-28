package com.github.SiddTiwari.notification.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpNotificationRequest(@NotBlank @Email String email, @NotBlank String otp, String name) {}
