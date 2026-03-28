package com.github.SiddTiwari.payment.web;

import com.github.SiddTiwari.payment.service.PaymentService;
import com.github.SiddTiwari.payment.web.dto.PaymentResponse;
import com.github.SiddTiwari.payment.web.dto.PaymentStatusRequest;
import com.github.SiddTiwari.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse byBooking(@PathVariable Long bookingId, @AuthenticationPrincipal AuthenticatedUser user) {
        return paymentService.getByBookingId(bookingId, user);
    }

    @GetMapping("/me")
    public List<PaymentResponse> myPayments(@AuthenticationPrincipal AuthenticatedUser user) {
        return paymentService.myPayments(user.email());
    }

    @PostMapping("/{id}/mock-success")
    public PaymentResponse mockSuccess(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        return paymentService.updateStatus(id, "PAID", user);
    }

    @PostMapping("/{id}/mock-failure")
    public PaymentResponse mockFailure(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        return paymentService.updateStatus(id, "FAILED", user);
    }

    @PostMapping("/{id}/status")
    public PaymentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody PaymentStatusRequest request, @AuthenticationPrincipal AuthenticatedUser user) {
        return paymentService.updateStatus(id, request.status(), user);
    }
}
