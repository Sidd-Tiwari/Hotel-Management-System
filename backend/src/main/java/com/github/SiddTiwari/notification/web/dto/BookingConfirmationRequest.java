package com.github.SiddTiwari.notification.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingConfirmationRequest(@NotBlank @Email String email, @NotBlank String roomName, @NotNull LocalDate checkIn, @NotNull LocalDate checkOut, @NotNull BigDecimal totalAmount) {}
