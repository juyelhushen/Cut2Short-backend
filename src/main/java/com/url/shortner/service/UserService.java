package com.url.shortner.service;

import com.url.shortner.payload.UserRequest;
import com.url.shortner.payload.UserUpdateRequest;
import com.url.shortner.wrapper.AuthResponse;
import com.url.shortner.wrapper.ProfileResponse;

public interface UserService {

    AuthResponse login(UserRequest request);
    AuthResponse register(UserRequest request);
    ProfileResponse fetchUserProfileInfo(String token);
    boolean updateProfile(UserUpdateRequest request, String token);
}
