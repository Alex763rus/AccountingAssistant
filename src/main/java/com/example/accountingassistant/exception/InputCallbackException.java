package com.example.accountingassistant.exception;

public class InputCallbackException extends RuntimeException {

    private final static String CALLBACK_EXCEPTION_TEXT = "Отсутствует ожидаемое нажатие на кнопку";

    public InputCallbackException() {
        super(CALLBACK_EXCEPTION_TEXT);
    }
}
