package com.github.SiddTiwari.room.service;

import com.github.SiddTiwari.room.domain.Room;
import com.github.SiddTiwari.room.domain.RoomType;
import com.github.SiddTiwari.room.repository.RoomRepository;
import com.github.SiddTiwari.room.web.dto.RoomRequest;
import com.github.SiddTiwari.room.web.dto.RoomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<RoomResponse> search(RoomType type, BigDecimal minPrice, BigDecimal maxPrice, boolean onlyActive) {
        return roomRepository.search(type, minPrice, maxPrice, onlyActive).stream().map(this::toResponse).toList();
    }

    public Room getEntity(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public RoomResponse get(Long id) { return toResponse(getEntity(id)); }

    public List<RoomResponse> all() {
        return roomRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Room::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponse create(RoomRequest request) {
        Room room = Room.builder()
                .roomNumber(request.roomNumber())
                .name(request.name())
                .type(request.type())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .active(request.active())
                .imageUrl(request.imageUrl())
                .description(request.description())
                .amenities(request.amenities())
                .build();
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = getEntity(id);
        room.setRoomNumber(request.roomNumber());
        room.setName(request.name());
        room.setType(request.type());
        room.setPricePerNight(request.pricePerNight());
        room.setCapacity(request.capacity());
        room.setActive(request.active());
        room.setImageUrl(request.imageUrl());
        room.setDescription(request.description());
        room.setAmenities(request.amenities());
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public void delete(Long id) {
        Room room = getEntity(id);
        room.setActive(false);
        roomRepository.save(room);
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(room.getId(), room.getRoomNumber(), room.getName(), room.getType(), room.getPricePerNight(), room.getCapacity(), room.isActive(), room.getImageUrl(), room.getDescription(), room.getAmenities());
    }
}
