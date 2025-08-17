package com.url.shortner.controller;

import com.url.shortner.entity.User;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.security.user.CustomOAuth2UserService;
import com.url.shortner.service.UserService;
import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<APIResponse> register(@RequestBody UserRequest request) {
        AuthResponse response = userService.register(request);
        APIResponse apiResponse = new APIResponse(true, Constant.REGISTRATION_SUCCESS, HttpStatus.OK.value(), response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody UserRequest request) {
        AuthResponse response = userService.login(request);
        APIResponse apiResponse = new APIResponse(true, Constant.LOGIN_SUCCESS, HttpStatus.OK.value(), response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(name = "AUTH-TOKEN", required = false) String token) {
        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No auth token");

        String email = jwtUtils.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName(),
                "profile", user.getImageUrl(),
                "role", user.getRole()
        ));
    }
}
