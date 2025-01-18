package com.url.shortner.controller;

import com.url.shortner.payload.UserRequest;
import com.url.shortner.service.UserService;
import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Map<String, Object> info(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

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


}
