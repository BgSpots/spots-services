package com.spots.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;

/** Object returned from the api in case error occurs */
@Getter
class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String path;

    ApiError(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = message;
        this.path = path;
    }
}
