package com.spots.service.auth;

public class InvalidLoginCredenials extends RuntimeException {
    public InvalidLoginCredenials(String message) {
        super(message);
    }
}
