package com.github.SiddTiwari.room.web;

import com.github.SiddTiwari.room.service.DiscountService;
import com.github.SiddTiwari.room.service.RoomService;
import com.github.SiddTiwari.room.web.dto.DiscountRequest;
import com.github.SiddTiwari.room.web.dto.DiscountResponse;
import com.github.SiddTiwari.room.web.dto.RoomRequest;
import com.github.SiddTiwari.room.web.dto.RoomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final RoomService roomService;
    private final DiscountService discountService;

    @GetMapping("/rooms")
    public List<RoomResponse> rooms() { return roomService.all(); }

    @PostMapping("/rooms")
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) { return roomService.create(request); }

    @PutMapping("/rooms/{id}")
    public RoomResponse updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) { return roomService.update(id, request); }

    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) { roomService.delete(id); }

    @GetMapping("/discounts")
    public List<DiscountResponse> discounts() { return discountService.all(); }

    @PostMapping("/discounts")
    public DiscountResponse createDiscount(@Valid @RequestBody DiscountRequest request) { return discountService.create(request); }

    @PutMapping("/discounts/{id}")
    public DiscountResponse updateDiscount(@PathVariable Long id, @Valid @RequestBody DiscountRequest request) { return discountService.update(id, request); }

    @DeleteMapping("/discounts/{id}")
    public void deleteDiscount(@PathVariable Long id) { discountService.delete(id); }
}
