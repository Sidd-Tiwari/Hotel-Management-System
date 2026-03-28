package com.github.SiddTiwari.payment.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentStatusRequest(@NotBlank String status) {}
