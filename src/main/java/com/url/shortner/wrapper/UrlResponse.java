package com.url.shortner.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.url.shortner.entity.Url;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record UrlResponse(int id, String title, String originalUrl, String shortenUrl,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
                          LocalDateTime createdAt,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
                          LocalDateTime updatedAt
) {


    public UrlResponse(Url url) {
        this(url.getId(), url.getTitle(), url.getOriginalUrl(), "http://localhost:8080/c2s/" + url.getShortenUrl(),
                LocalDateTime.ofInstant(url.getCreatedDate(), ZoneId.of("UTC")),
                LocalDateTime.ofInstant(url.getLastModifiedDate(), ZoneId.of("UTC"))
        );
    }
}
