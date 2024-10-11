package com.url.shortner.service;

import com.url.shortner.entity.Url;
import com.url.shortner.payload.UrlRequest;

import java.util.List;

public interface UrlService {

    public List<Url> getUrls();
    public String filterUrl(String OUrl);
    public String createUrl(String filteredUrl, String originalUrl);
}
