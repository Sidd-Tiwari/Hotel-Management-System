package com.github.SiddTiwari.payment.web.dto;

import com.github.SiddTiwari.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(Long id, Long bookingId, BigDecimal amount, PaymentStatus status, String method, String transactionRef, OffsetDateTime createdAt) {}
