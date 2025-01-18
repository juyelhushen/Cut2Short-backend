package com.url.shortner.service;

import com.url.shortner.entity.Url;
import com.url.shortner.payload.UrlRequest;
import com.url.shortner.wrapper.UrlResponse;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UrlService {

    List<UrlResponse> findAllUrl();

    public String filterUrl(UrlRequest request);

    public UrlResponse createUrl(String filteredUrl, String originalUrl);

    //    public String shortenUrl(UrlRequest request) throws NoSuchAlgorithmException;
    CompletableFuture<String> getOriginalUrl(String shortUrl);

}
