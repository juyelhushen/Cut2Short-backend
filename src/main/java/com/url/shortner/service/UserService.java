package com.url.shortner.service;

import com.url.shortner.payload.UserRequest;
import com.url.shortner.wrapper.AuthResponse;

public interface UserService {

    public AuthResponse login(UserRequest request);
    public AuthResponse register(UserRequest request);
}
