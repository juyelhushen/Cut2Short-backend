package com.url.shortner.controller;

import com.url.shortner.payload.UrlRequest;
import com.url.shortner.service.UrlService;
import com.url.shortner.wrapper.UrlResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/url")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlController {

    private final UrlService urlService;

    public ResponseEntity<List<UrlResponse>> findAllUrl() {
        List<UrlResponse> responses = urlService.findAllUrl();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> createdUrl(@RequestBody UrlRequest request) {
        try {
            String filterUrl =  urlService.filterUrl(request);
            UrlResponse response = urlService.createUrl(filterUrl, request.originalUrl());
            log.info("filter url is - ", filterUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    };

    @GetMapping("/redirect/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalUrl(shortUrl);
        response.sendRedirect(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

}
