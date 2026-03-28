package com.github.SiddTiwari.booking.web.dto;

import java.util.List;

public record AvailabilitySearchResponse(List<RoomSummary> rooms) {
    public record RoomSummary(Long id, String roomNumber, String name, String type, java.math.BigDecimal pricePerNight, Integer capacity, boolean active, String imageUrl, String description, String amenities) {}
}
