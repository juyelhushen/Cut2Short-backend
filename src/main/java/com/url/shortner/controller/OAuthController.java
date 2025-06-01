package com.url.shortner.controller;

import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OAuthController {

    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> handleOAuth2Login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User principal)) {
            log.error("User is not authenticated. SecurityContextHolder: {}", SecurityContextHolder.getContext());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        log.info("User is authenticated: email={}", (Object) principal.getAttribute("email"));

        String email = principal.getAttribute("email");
        if (email == null) {
            log.warn("Email not provided, attempting fallback.");
            email = principal.getAttribute("login") + "@github.com";
        }

        Integer userId = principal.getAttribute("userId");
        String name = principal.getAttribute("name");
        String token = principal.getAttribute("token");
        String profile = principal.getAttribute("profile");

        if (userId == null || token == null) {
            log.error("Missing userId or token in OAuth2User attributes: userId={}, token={}", userId, token);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Missing user data");
        }

        AuthResponse auth = new AuthResponse(userId, name, email, token, profile);
        APIResponse response = new APIResponse(true, Constant.LOGIN_SUCCESS, HttpStatus.CREATED.value(), auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}