package com.url.shortner.wrapper;

import com.url.shortner.entity.User;

import java.util.Base64;

public record ProfileResponse(
        int id,
        String name,
        String email,
        String phoneNumber,
        String profileUrl,
        String profileBase64Data,
        String role
) {
    public ProfileResponse(User user) {
        this(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getImageUrl() != null ? user.getImageUrl() : "",
                user.getImage() != null ? Base64.getEncoder().encodeToString(user.getImage()) : "",
                user.getRole().name()
        );
    }
}
