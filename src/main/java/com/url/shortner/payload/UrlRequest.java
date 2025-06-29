package com.url.shortner.payload;

import lombok.Builder;

@Builder
public record UrlRequest(int id, String originalUrl, String title,String suffix, String email) {
}
