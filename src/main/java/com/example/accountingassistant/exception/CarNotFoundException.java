package com.example.accountingassistant.exception;

public class CarNotFoundException extends RuntimeException {

    private final static String CAR_NOT_FOUND = "Машина не найдена в настройках: ";
    public CarNotFoundException(String message) {
        super(CAR_NOT_FOUND + message);
    }
}
