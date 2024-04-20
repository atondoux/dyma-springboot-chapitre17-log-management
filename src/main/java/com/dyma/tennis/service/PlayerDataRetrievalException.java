package com.dyma.tennis.service;

public class PlayerDataRetrievalException extends RuntimeException {

    public PlayerDataRetrievalException(Exception e) {
        super("Could not retrieve player data", e);
    }

}
