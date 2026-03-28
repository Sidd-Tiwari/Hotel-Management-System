package com.github.SiddTiwari.room.web.dto;

import java.math.BigDecimal;

public record DiscountValidationResponse(boolean valid, String code, BigDecimal percentage, BigDecimal discountAmount, String message) {}
