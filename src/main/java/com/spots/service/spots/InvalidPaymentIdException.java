package com.spots.service.spots;

public class InvalidPaymentIdException extends RuntimeException {
    public InvalidPaymentIdException(String message) {
        super(message);
    }
}
