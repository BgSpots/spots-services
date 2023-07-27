package com.spots.service.spots;

public class InvalidSpotNameException extends RuntimeException {
    public InvalidSpotNameException(String message) {
        super(message);
    }
}
