package com.spots.service.spots;

public class InvalidReviewIdException extends RuntimeException{
    public InvalidReviewIdException(String message) {
        super(message);
    }
}
