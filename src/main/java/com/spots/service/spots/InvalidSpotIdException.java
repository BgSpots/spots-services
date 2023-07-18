package com.spots.service.spots;

public class InvalidSpotIdException extends RuntimeException {
    public InvalidSpotIdException(String message) {
        super(message);
    }
}
