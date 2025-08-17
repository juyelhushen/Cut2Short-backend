package com.url.shortner.security.cookie;

import org.springframework.http.ResponseCookie;

public interface CookieService {
    ResponseCookie createCookie(String token);
}