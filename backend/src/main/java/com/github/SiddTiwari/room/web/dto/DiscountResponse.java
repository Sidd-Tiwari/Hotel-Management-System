package com.github.SiddTiwari.room.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DiscountResponse(Long id, String code, BigDecimal percentage, BigDecimal minimumBookingAmount, OffsetDateTime expiresAt, boolean active, String description) {}
