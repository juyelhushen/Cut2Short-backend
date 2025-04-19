package com.url.shortner.controller;

import com.url.shortner.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/c2s")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortUrl}")
    public CompletableFuture<ResponseEntity<Void>> redirect(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl).thenApply(originalUrl -> {
            if (originalUrl.isBlank()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        });
    }
}
