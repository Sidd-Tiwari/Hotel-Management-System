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

        try {
            Resend resend = new Resend(appProperties.getResend().getApiKey());

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(appProperties.getResend().getFromEmail())
                    .to(toEmail)
                    .subject("Your OTP Code")
                    .html("""
                            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                                <h2>Your OTP Code</h2>
                                <p>Your OTP is: <strong>%s</strong></p>
                                <p>This OTP expires in 5 minutes.</p>
                            </div>
                            """.formatted(otp))
                    .build();

            resend.emails().send(params);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to send OTP email", ex);
        }
    }
}
