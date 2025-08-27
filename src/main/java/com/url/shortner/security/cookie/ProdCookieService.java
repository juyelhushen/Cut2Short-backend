package com.url.shortner.security.cookie;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Profile("prod")
public class ProdCookieService implements CookieService {

    @Override
    public ResponseCookie createCookie(String token) {
        return ResponseCookie.from("AUTH-TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
//                .domain(".onrender.com")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
