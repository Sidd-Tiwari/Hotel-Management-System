package com.github.SiddTiwari.booking.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsResponse(long totalBookings, BigDecimal totalRevenue, long pendingBookings, List<AnalyticsPoint> monthly) {}
