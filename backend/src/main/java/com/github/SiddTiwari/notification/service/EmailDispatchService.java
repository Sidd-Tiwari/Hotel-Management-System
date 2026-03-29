package com.github.SiddTiwari.notification.service;

import com.github.SiddTiwari.config.AppProperties;
import com.github.SiddTiwari.notification.web.dto.BookingConfirmationRequest;
import com.github.SiddTiwari.notification.web.dto.OtpNotificationRequest;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailDispatchService {

    private final AppProperties properties;

    public void sendOtp(OtpNotificationRequest request) {
        String greeting = request.name() == null || request.name().isBlank() ? "Guest" : request.name();

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #222;">HotelHub OTP Verification</h2>
                    <p>Hello %s,</p>
                    <p>Your OTP is:</p>
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #0d6efd; margin: 20px 0;">
                        %s
                    </div>
                    <p>It will expire in a few minutes.</p>
                    <p>If you did not request this OTP, please ignore this email.</p>
                    <br/>
                    <p>Thanks,<br/>HotelHub</p>
                </div>
                """.formatted(greeting, request.otp());

        send(request.email(), "Your HotelHub OTP", html);
    }

    public void sendBookingConfirmation(BookingConfirmationRequest request) {
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #222;">Booking Confirmed</h2>
                    <p>Your booking is confirmed.</p>
                    <table style="border-collapse: collapse; width: 100%%; margin-top: 15px;">
                        <tr>
                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Room</strong></td>
                            <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Check-in</strong></td>
                            <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Check-out</strong></td>
                            <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Amount</strong></td>
                            <td style="padding: 8px; border: 1px solid #ddd;">₹%s</td>
                        </tr>
                    </table>
                    <br/>
                    <p>We look forward to hosting you.</p>
                    <p>Thanks,<br/>HotelHub</p>
                </div>
                """.formatted(
                request.roomName(),
                request.checkIn(),
                request.checkOut(),
                request.totalAmount()
        );

        send(request.email(), "Booking Confirmed - " + request.roomName(), html);
    }

    private void send(String to, String subject, String html) {
        if (properties.getMail().isMockMode()) {
            log.info("Mock email -> to={}, subject={}, html={}", to, subject, html);
            return;
        }

        if (!properties.getResend().isEnabled()) {
            log.info("Resend disabled. Skipping email -> to={}, subject={}", to, subject);
            return;
        }

        try {
            Resend resend = new Resend(properties.getResend().getApiKey());

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(properties.getResend().getFromEmail())
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            resend.emails().send(params);
            log.info("Email sent successfully -> to={}, subject={}", to, subject);

        } catch (Exception ex) {
            log.error("Failed to send email -> to={}, subject={}", to, subject, ex);
            throw new RuntimeException("Failed to send email", ex);
        }
    }
}
