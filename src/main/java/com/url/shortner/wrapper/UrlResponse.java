package com.url.shortner.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.url.shortner.entity.Url;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public record UrlResponse(int id, String title, String originalUrl, String shortenUrl,
                          String suffix,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM, yyyy")
                          LocalDateTime createdAt,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM, yyyy")
                          LocalDateTime updatedAt,
                          long hitCount
) {

    public UrlResponse(Url url, String BASE_URL) {
        this(url.getId(), url.getTitle(), url.getOriginalUrl(),
                BASE_URL + url.getShortenUrl(),
                url.getSuffix(),
                LocalDateTime.ofInstant(Objects.requireNonNull(url.getCreatedDate()), ZoneId.of("UTC")),
                LocalDateTime.ofInstant(Objects.requireNonNull(url.getLastModifiedDate()), ZoneId.of("UTC")),
                url.getHitCount()
        );
    }
}
