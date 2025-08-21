package com.url.shortner.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.url.shortner.entity.QRCode;
import com.url.shortner.entity.Url;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Objects;

public record QRCodeResponse(Long id,
                             String title,
                             String originalUrl,
                             String shortUrl,
                             String byteData,
                             boolean active,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy")
                             LocalDateTime createdOn
) {
    public QRCodeResponse(Url url) {
        this(
                Objects.requireNonNull(url.getQrCode()).getId(),
                url.getTitle(),
                url.getOriginalUrl(),
                url.getShortenUrl(),
                Base64.getEncoder().encodeToString(Objects.requireNonNull(url.getQrCode()).getQrCode()),
                LocalDate.now().isBefore(url.getExpires()),
                LocalDateTime.ofInstant(Objects.requireNonNull(url.getCreatedDate()), ZoneId.of("UTC"))
        );
    }
}
