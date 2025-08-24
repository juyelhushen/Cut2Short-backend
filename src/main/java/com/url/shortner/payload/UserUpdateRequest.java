package com.url.shortner.payload;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String mobileNo,
        String base6dProfile
) {
}
