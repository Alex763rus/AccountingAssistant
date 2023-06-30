package com.example.accountingassistant.exception;

import lombok.val;

import static com.example.accountingassistant.constant.Constant.NEW_LINE;

public class InputCallbackException extends RuntimeException {

    private final static String CALLBACK_EXCEPTION_TEXT = "Отсутствует ожидаемое нажатие на кнопку";

    public InputCallbackException() {
        super(CALLBACK_EXCEPTION_TEXT);
    }
}
