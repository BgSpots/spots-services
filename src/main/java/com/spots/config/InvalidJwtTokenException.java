package com.spots.config;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
