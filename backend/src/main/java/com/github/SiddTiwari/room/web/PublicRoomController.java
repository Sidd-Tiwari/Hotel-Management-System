package com.github.SiddTiwari.room.web;

import com.github.SiddTiwari.booking.service.BookingService;
import com.github.SiddTiwari.booking.web.dto.AvailabilitySearchResponse;
import com.github.SiddTiwari.room.domain.RoomType;
import com.github.SiddTiwari.room.service.DiscountService;
import com.github.SiddTiwari.room.service.RoomService;
import com.github.SiddTiwari.room.web.dto.DiscountResponse;
import com.github.SiddTiwari.room.web.dto.DiscountValidationResponse;
import com.github.SiddTiwari.room.web.dto.RoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicRoomController {

    private final RoomService roomService;
    private final DiscountService discountService;
    private final BookingService bookingService;

    @GetMapping("/rooms")
    public List<RoomResponse> rooms(@RequestParam(required = false) RoomType type,
                                    @RequestParam(required = false) BigDecimal minPrice,
                                    @RequestParam(required = false) BigDecimal maxPrice) {
        return roomService.search(type, minPrice, maxPrice, true);
    }

    @GetMapping("/rooms/{id}")
    public RoomResponse room(@PathVariable Long id) {
        return roomService.get(id);
    }

    @GetMapping("/offers")
    public List<DiscountResponse> offers() {
        return discountService.publicOffers();
    }

    @GetMapping("/discounts/validate")
    public DiscountValidationResponse validateDiscount(@RequestParam String code, @RequestParam BigDecimal amount) {
        return discountService.validate(code, amount);
    }

    @GetMapping("/bookings/search-availability")
    public AvailabilitySearchResponse searchAvailability(@RequestParam(required = false) LocalDate checkIn,
                                                         @RequestParam(required = false) LocalDate checkOut,
                                                         @RequestParam(required = false) String type,
                                                         @RequestParam(required = false) BigDecimal minPrice,
                                                         @RequestParam(required = false) BigDecimal maxPrice) {
        return bookingService.searchAvailability(checkIn, checkOut, type, minPrice, maxPrice);
    }
}
