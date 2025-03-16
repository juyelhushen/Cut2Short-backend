package com.url.shortner.payload;

public record UrlRequest(int id, String originalUrl, String title,String tags, int userId) {
}
