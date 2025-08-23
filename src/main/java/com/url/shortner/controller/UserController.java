package com.url.shortner.controller;

import com.url.shortner.entity.User;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.security.JwtUtils;
import com.url.shortner.security.cookie.CookieService;
import com.url.shortner.security.user.CustomOAuth2UserService;
import com.url.shortner.service.UserService;
import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Objects;

@Slf4j
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

        log.info("Response: {}", response.toString());

        APIResponse apiResponse = new APIResponse(
                true,
                "Registration successful",
                HttpStatus.OK.value(),response
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody UserRequest request) {
        AuthResponse response = userService.login(request);

        log.info("Response: {}", response.toString());
        APIResponse apiResponse = new APIResponse(
                true,
                "Login successful",
                HttpStatus.OK.value(),
                response
        );

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


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/profile/info")
    public ResponseEntity<APIResponse> profileInfo(@CookieValue(name = "AUTH-TOKEN", required = false) String token) {
        var profileInfo = userService.fetchUserProfileInfo(token);
        APIResponse apiResponse = new APIResponse(true, Constant.DATA_FETCH_SUCCESS, HttpStatus.OK.value(), profileInfo);
        return ResponseEntity.ok(apiResponse);
    }
}
