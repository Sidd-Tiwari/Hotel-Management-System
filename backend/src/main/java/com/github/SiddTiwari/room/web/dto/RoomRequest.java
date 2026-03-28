package com.github.SiddTiwari.room.web.dto;

import com.github.SiddTiwari.room.domain.RoomType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RoomRequest(
        @NotBlank String roomNumber,
        @NotBlank @Size(max = 120) String name,
        @NotNull RoomType type,
        @NotNull @DecimalMin("0.0") BigDecimal pricePerNight,
        @NotNull @Min(1) Integer capacity,
        boolean active,
        @Size(max = 500) String imageUrl,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Size(max = 1000) String amenities
) {}
