package com.spots.service.spots;

public class SpotRerollAlreadyUsed extends RuntimeException {
    public SpotRerollAlreadyUsed(String message) {
        super(message);
    }
}
