package com.github.SiddTiwari.room.web.dto;

import com.github.SiddTiwari.room.domain.RoomType;
import java.math.BigDecimal;

public record RoomResponse(Long id, String roomNumber, String name, RoomType type, BigDecimal pricePerNight, Integer capacity, boolean active, String imageUrl, String description, String amenities) {}
