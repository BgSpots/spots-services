package com.spots.service.spots;

public class RandomSpotIsNotAvailableYet extends RuntimeException {
    public RandomSpotIsNotAvailableYet(String message) {
        super(message);
    }
}
