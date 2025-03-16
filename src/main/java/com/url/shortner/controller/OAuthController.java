package com.url.shortner.controller;

import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OAuthController {

    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> handleOAuth2Login(OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null || !authenticationToken.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        OAuth2User principal = authenticationToken.getPrincipal();
        log.info("User is authenticated");

        String email = principal.getAttribute("email");
        if (email == null) {
            log.warn("Email not provided, attempting fallback.");
            email = principal.getAttribute("login") + "@github.com";
        }

        int userId = principal.getAttribute("userid");
        String name = principal.getAttribute("name");
        var token = principal.getAttribute("token");
        assert token != null;

        AuthResponse auth = new AuthResponse(userId, name,email,token.toString());
        APIResponse response = new APIResponse(true, Constant.LOGIN_SUCCESS,HttpStatus.CREATED.value(), auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}