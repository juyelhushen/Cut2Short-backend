package com.url.shortner.service;

import com.url.shortner.payload.UrlRequest;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.wrapper.UrlResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UrlService {

    List<UrlResponse> findAllUrl();

    public String filterUrl(UrlRequest request);

    public UrlResponse createUrlForUser(String filteredUrl, UrlRequest request, String username);

    public UrlResponse createUrl(String filteredUrl, String originalUrl);

    //    public String shortenUrl(UrlRequest request) throws NoSuchAlgorithmException;
    CompletableFuture<String> getOriginalUrl(String shortUrl);

    List<UrlResponse> findAllUrlByUserId(int userId);

    boolean deleteUrlById(int id);

    UrlResponse updateUrl(UrlRequest request);
}
