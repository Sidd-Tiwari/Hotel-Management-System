package com.github.SiddTiwari.notification;

import com.github.SiddTiwari.config.AppProperties;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.stereotype.Service;

@Service
public class ResendMailService {

    private final AppProperties appProperties;

    public ResendMailService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void sendOtp(String toEmail, String otp) {
        if (appProperties.getMail().isMockMode()) {
            System.out.println("Mock mail mode enabled. OTP for " + toEmail + " = " + otp);
            return;
        }

        if (!appProperties.getResend().isEnabled()) {
            return;
        }

        Resend resend = new Resend(appProperties.getResend().getApiKey());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(appProperties.getResend().getFromEmail())
                .to(toEmail)
                .subject("Your OTP Code")
                .html("""
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                            <h2 style="color: #222;">Hotel Management System</h2>
                            <p>Your OTP for login is:</p>
                            <h1 style="letter-spacing: 4px; color: #0d6efd;">%s</h1>
                            <p>This OTP is valid for a limited time only.</p>
                            <p>If you did not request this, please ignore this email.</p>
                        </div>
                        """.formatted(otp))
                .build();

        resend.emails().send(params);
    }
}
