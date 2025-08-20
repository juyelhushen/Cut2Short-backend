package com.url.shortner.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(String error, int code,
                            @JsonFormat(pattern = "dd MMM, yyyy hh:mm:ss")
                            LocalDateTime timestamp) {

    public ErrorResponse(String error,  int code) {
        this(error, code, LocalDateTime.now());
    }
}
