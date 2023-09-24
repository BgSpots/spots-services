package com.spots.service.payment;

public class InvalidPaymentIdException extends RuntimeException {
    public InvalidPaymentIdException(String message) {
        super(message);
    }
}
