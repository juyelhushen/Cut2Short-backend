package com.url.shortner.security.cookie;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("dev")
public class DevCookieService implements CookieService {

    @Override
    public ResponseCookie createCookie(String token) {
        return ResponseCookie.from("AUTH-TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
