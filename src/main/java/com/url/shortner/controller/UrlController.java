package com.url.shortner.controller;

import com.url.shortner.payload.UrlRequest;
import com.url.shortner.service.UrlService;
import com.url.shortner.utils.APIResponse;
import com.url.shortner.utils.Constant;
import com.url.shortner.wrapper.UrlResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlController {

    private final UrlService urlService;

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UrlResponse>> findAllUrl() {
        List<UrlResponse> responses = urlService.findAllUrl();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/shorten/set")
    public ResponseEntity<APIResponse> createUrl(@RequestBody UrlRequest request) {
        try {
            var filterUrl = urlService.filterUrl(request);
            var response = urlService.createUrlForUser(filterUrl, request);
            var apiResponse = new APIResponse(true, Constant.DATA_FETCH_SUCCESS, HttpStatus.OK.value(), response);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @PostMapping("/shorten")
    public ResponseEntity<APIResponse> createdUrl(@RequestBody UrlRequest request) {
        try {
            var filterUrl = urlService.filterUrl(request);
            var response = urlService.createUrl(filterUrl, request.originalUrl());
            var apiResponse = new APIResponse(true, Constant.DATA_FETCH_SUCCESS, HttpStatus.OK.value(), response);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<APIResponse> getUrlById(@PathVariable int userId) {
        try {
            var response = urlService.findAllUrlByUserId(userId);
            var apiResponse = new APIResponse(true, Constant.DATA_FETCH_SUCCESS, HttpStatus.OK.value(), response);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteUrl(@PathVariable int id) {
        var response = urlService.deleteUrlById(id);
        APIResponse apiResponse;
        if (response) {
            apiResponse = new APIResponse(true, Constant.DATA_DELETED_SUCCESSFULLY, HttpStatus.OK.value(), null);
        } else apiResponse = new APIResponse(false, Constant.FAILED_TO_DELETED, HttpStatus.OK.value(), null);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/update")
    public ResponseEntity<APIResponse> updateUrl(@RequestBody UrlRequest request) {
        var response = urlService.updateUrl(request);
        var apiResponse = new APIResponse(true, Constant.DATA_UPDATE_SUCCESS, HttpStatus.OK.value(), response);
        return ResponseEntity.ok(apiResponse);
    }
}
