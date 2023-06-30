package com.example.accountingassistant.exception;

import lombok.val;

import static com.example.accountingassistant.constant.Constant.NEW_LINE;

public class InputLongException extends RuntimeException {
    public InputLongException(String message, int maxValue) {
        super(getCheckPhoneErrorMessageText(message, maxValue));
    }

    private static String getCheckPhoneErrorMessageText(String message, int maxValue) {
        val errorText = new StringBuilder();
        errorText.append("Введено некорректное значение: ").append(message).append(NEW_LINE)
                .append("Требования к искомому значению:").append(NEW_LINE)
                .append("- введенное значение должно быть числом").append(NEW_LINE)
                .append("- количество символов в числе должно быть не больше ").append(maxValue).append(NEW_LINE).append(NEW_LINE)
                .append("Повторите ввод:");
        return errorText.toString();
    }
}
