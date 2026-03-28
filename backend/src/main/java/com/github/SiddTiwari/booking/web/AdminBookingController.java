package com.github.SiddTiwari.booking.web;

import com.github.SiddTiwari.booking.service.BookingService;
import com.github.SiddTiwari.booking.web.dto.AnalyticsResponse;
import com.github.SiddTiwari.booking.web.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {
    private final BookingService bookingService;

    @GetMapping("/bookings")
    public List<BookingResponse> all() { return bookingService.allBookings(); }

    @PatchMapping("/bookings/{bookingId}/check-in")
    public BookingResponse checkIn(@PathVariable Long bookingId) { return bookingService.checkIn(bookingId); }

    @PatchMapping("/bookings/{bookingId}/check-out")
    public BookingResponse checkOut(@PathVariable Long bookingId) { return bookingService.checkOut(bookingId); }

    @GetMapping("/analytics")
    public AnalyticsResponse analytics() { return bookingService.analytics(); }
}
