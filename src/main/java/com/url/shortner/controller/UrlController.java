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

    @PostMapping("/shorten")
    public ResponseEntity<APIResponse> createdUrl(@RequestBody UrlRequest request) {
        try {
            String filterUrl =  urlService.filterUrl(request);
            UrlResponse response = urlService.createUrl(filterUrl, request.originalUrl());
            log.info("filter url is - ", filterUrl);
            APIResponse apiResponse = new APIResponse(true, Constant.DATA_FETCH_SUCCESS,HttpStatus.OK.value(),response);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    };

}
