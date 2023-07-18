package com.spots.service.spots;

public class NoReviewsException extends RuntimeException{
    public NoReviewsException(String message) {
        super(message);
    }
}
