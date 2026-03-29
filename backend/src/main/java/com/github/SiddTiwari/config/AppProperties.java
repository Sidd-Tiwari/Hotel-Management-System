package com.github.SiddTiwari.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Otp otp = new Otp();
    private final Bootstrap bootstrap = new Bootstrap();
    private final Cors cors = new Cors();
    private final Mail mail = new Mail();
    private final Resend resend = new Resend();

    @Getter
    @Setter
    public static class Jwt {
        @NotBlank
        private String secret;

        @NotBlank
        private String issuer;

        @Min(300)
        private long accessTokenSeconds = 28800;
    }

    @Getter
    @Setter
    public static class Otp {
        @Min(60)
        private long ttlSeconds = 300;

        @Min(4)
        private int length = 6;

        @Min(10)
        private long resendCooldownSeconds = 60;
    }

    @Getter
    @Setter
    public static class Bootstrap {
        private String adminEmail;
    }

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";
    }

    @Getter
    @Setter
    public static class Mail {
        private boolean mockMode = true;
    }

    @Getter
    @Setter
    public static class Resend {
        @NotBlank
        private String apiKey;

        @NotBlank
        private String fromEmail;

        private boolean enabled = true;
    }
}
