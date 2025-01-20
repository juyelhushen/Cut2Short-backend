package com.url.shortner.payload;

public record UserRequest(String firstName, String lastName, String email, String password) {
}
