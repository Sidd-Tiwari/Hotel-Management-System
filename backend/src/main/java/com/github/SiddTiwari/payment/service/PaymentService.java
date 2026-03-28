package com.github.SiddTiwari.payment.service;

import com.github.SiddTiwari.booking.domain.Booking;
import com.github.SiddTiwari.booking.domain.BookingStatus;
import com.github.SiddTiwari.booking.repository.BookingRepository;
import com.github.SiddTiwari.notification.service.EmailDispatchService;
import com.github.SiddTiwari.notification.web.dto.BookingConfirmationRequest;
import com.github.SiddTiwari.payment.domain.Payment;
import com.github.SiddTiwari.payment.domain.PaymentStatus;
import com.github.SiddTiwari.payment.repository.PaymentRepository;
import com.github.SiddTiwari.payment.web.dto.PaymentResponse;
import com.github.SiddTiwari.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final EmailDispatchService emailDispatchService;

    @Transactional
    public Payment createPendingPayment(Booking booking, BigDecimal amount) {
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .method("MOCK")
                .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .build();
        return paymentRepository.save(payment);
    }

    public PaymentResponse getByBookingId(Long bookingId, AuthenticatedUser currentUser) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        ensureOwnerOrAdmin(payment, currentUser);
        return toResponse(payment);
    }

    public List<PaymentResponse> myPayments(String email) {
        return paymentRepository.findAllByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> all() {
        return paymentRepository.findAllDetailed().stream().map(this::toResponse).toList();
    }

    @Transactional
    public PaymentResponse updateStatus(Long id, String status, AuthenticatedUser currentUser) {
        Payment payment = paymentRepository.findDetailedById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        ensureOwnerOrAdmin(payment, currentUser);

        String normalized = status.trim().toUpperCase();
        Booking booking = payment.getBooking();
        if ("PAID".equals(normalized)) {
            payment.setStatus(PaymentStatus.PAID);
            booking.setStatus(BookingStatus.CONFIRMED);
            emailDispatchService.sendBookingConfirmation(new BookingConfirmationRequest(
                    booking.getUser().getEmail(),
                    booking.getRoom().getName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getTotalAmount()
            ));
        } else if ("FAILED".equals(normalized)) {
            payment.setStatus(PaymentStatus.FAILED);
            booking.setStatus(BookingStatus.CANCELLED);
        } else if ("REFUNDED".equals(normalized)) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment status");
        }

        bookingRepository.save(booking);
        return toResponse(paymentRepository.save(payment));
    }

    private void ensureOwnerOrAdmin(Payment payment, AuthenticatedUser currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        boolean owner = payment.getBooking().getUser().getId().equals(currentUser.userId());
        if (!owner && !currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getTransactionRef(),
                payment.getCreatedAt()
        );
    }
}
