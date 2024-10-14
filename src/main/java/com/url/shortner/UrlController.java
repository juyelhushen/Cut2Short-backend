package com.url.shortner;

import com.url.shortner.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody String originalUrl) {
        return "";
    }
}
