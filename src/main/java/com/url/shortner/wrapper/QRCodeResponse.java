package com.url.shortner.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.url.shortner.entity.QRCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Objects;

public record QRCodeResponse(Long id,
                             String title,
                             String originalUrl,
                             String byteData,
                             boolean active,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy")
                             LocalDateTime createdOn
) {
    public QRCodeResponse(QRCode qrCode) {
        this(
                qrCode.getId(),
                qrCode.getUrl().getTitle(),
                qrCode.getUrl().getOriginalUrl(),
                Base64.getEncoder().encodeToString(qrCode.getQrCode()),
                LocalDate.now().isBefore(qrCode.getUrl().getExpires()),
                LocalDateTime.ofInstant(Objects.requireNonNull(qrCode.getUrl().getCreatedDate()), ZoneId.of("UTC"))
        );
    }
}
