package com.github.SiddTiwari.payment.web;

import com.github.SiddTiwari.payment.service.PaymentService;
import com.github.SiddTiwari.payment.web.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> all() {
        return paymentService.all();
    }
}
