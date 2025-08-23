package com.url.shortner.wrapper;

import com.url.shortner.entity.User;

import java.util.Base64;
import java.util.Optional;

public record ProfileResponse(
        int id,
        String name,
        String email,
        String phoneNumber,
        String profileBase64Data,
        String role
) {
    public ProfileResponse(User user) {
        this(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                resolveProfileImage(user),
                user.getRole().name()
        );
    }

    public static String resolveProfileImage(User user) {
        return Optional.ofNullable(user.getImageUrl())
                .orElseGet(() -> {
                    var profileByte = user.getImage();
                    return profileByte != null ? Base64.getEncoder().encodeToString(profileByte) : "";
                });
    }
}
