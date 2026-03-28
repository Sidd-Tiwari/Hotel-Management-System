package com.github.SiddTiwari.room.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DiscountRequest(
        @NotBlank @Size(max = 40) String code,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal percentage,
        @NotNull @DecimalMin("0.0") BigDecimal minimumBookingAmount,
        @NotNull OffsetDateTime expiresAt,
        boolean active,
        @Size(max = 300) String description
) {}
