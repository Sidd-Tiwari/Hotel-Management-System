package com.github.SiddTiwari.notification.service;

import com.github.SiddTiwari.config.AppProperties;
import com.github.SiddTiwari.notification.web.dto.BookingConfirmationRequest;
import com.github.SiddTiwari.notification.web.dto.OtpNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailDispatchService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final AppProperties properties;

    @Value("${spring.mail.username:no-reply@hotelhub.com}")
    private String fromAddress;

    public void sendOtp(OtpNotificationRequest request) {
        String greeting = request.name() == null || request.name().isBlank() ? "Guest" : request.name();
        String body = "Hello " + greeting + ",\n\nYour OTP is: " + request.otp()
                + "\nIt will expire in a few minutes.\n\nThanks,\nHotelHub";
        send(request.email(), "Your HotelHub OTP", body);
    }

    public void sendBookingConfirmation(BookingConfirmationRequest request) {
        String body = "Your booking is confirmed.\n\nRoom: " + request.roomName()
                + "\nCheck-in: " + request.checkIn()
                + "\nCheck-out: " + request.checkOut()
                + "\nAmount: ₹" + request.totalAmount()
                + "\n\nWe look forward to hosting you.\n\nHotelHub";
        send(request.email(), "Booking Confirmed - " + request.roomName(), body);
    }

    private void send(String to, String subject, String body) {
        if (properties.getMail().isMockMode()) {
            log.info("Mock email -> to={}, subject={}, body={}", to, subject, body);
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Mail sender unavailable, logging instead -> to={}, subject={}, body={}", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Failed to send email", ex);
            throw ex;
        }
    }
}
