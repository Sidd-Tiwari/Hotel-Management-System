package com.github.SiddTiwari.booking.web.dto;

import com.github.SiddTiwari.booking.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BookingResponse(Long id, Long roomId, String roomName, String roomType, LocalDate checkInDate, LocalDate checkOutDate, Integer guests, BigDecimal baseAmount, BigDecimal discountAmount, BigDecimal totalAmount, String discountCode, BookingStatus status, Long paymentId, String paymentReference, OffsetDateTime createdAt) {}
