package com.github.SiddTiwari.booking.web;

import com.github.SiddTiwari.booking.service.BookingService;
import com.github.SiddTiwari.booking.web.dto.BookingResponse;
import com.github.SiddTiwari.booking.web.dto.CreateBookingRequest;
import com.github.SiddTiwari.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(user.userId(), request);
    }

    @GetMapping("/me")
    public List<BookingResponse> myBookings(@AuthenticationPrincipal AuthenticatedUser user) {
        return bookingService.myBookings(user.userId());
    }

    @DeleteMapping("/{bookingId}")
    public BookingResponse cancel(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long bookingId) {
        return bookingService.cancel(bookingId, user.userId());
    }
}
