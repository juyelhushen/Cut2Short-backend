package com.url.shortner.payload;

import com.url.shortner.entity.Url;

import java.util.Base64;
import java.util.Objects;

public record QRCodeRequest(Long id, String title, String url, String qrCodeBase64) {

    public QRCodeRequest(Url url) {
        this(Objects.requireNonNull(url.getQrCode()).getId(), url.getTitle(), url.getOriginalUrl(),
                Base64.getEncoder().encodeToString(url.getQrCode().getQrCode()));
    }
}
