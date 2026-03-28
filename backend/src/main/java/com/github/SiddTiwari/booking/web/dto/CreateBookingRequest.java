package com.github.SiddTiwari.booking.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(@NotNull Long roomId, @NotNull @Future LocalDate checkInDate, @NotNull @Future LocalDate checkOutDate, @NotNull @Min(1) Integer guests, String discountCode) {}
