package com.url.shortner.wrapper;

import com.url.shortner.entity.Role;

public record AuthResponse(
        int userId,
        String name,
        String username,
        String profile,
        Role role
) {
}
