package com.url.shortner.wrapper;

public record AuthResponse(
        int userId,
        String name,
        String username,
        String token
) {
}
