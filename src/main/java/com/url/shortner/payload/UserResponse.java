package com.url.shortner.payload;

public record UserResponse(
        Integer userId,
        String email,
        String name,
        String profile,
        String role
) {}
