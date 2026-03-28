package com.github.SiddTiwari.booking.web.dto;

import java.math.BigDecimal;

public record AnalyticsPoint(String label, long bookings, BigDecimal revenue) {}
