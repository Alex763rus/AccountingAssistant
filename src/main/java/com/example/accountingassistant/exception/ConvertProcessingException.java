package com.example.accountingassistant.exception;

public class ConvertProcessingException extends RuntimeException {
    private final static String CONVERT_PROCESSING_ERROR = "Ошибка обработки файла: ";
    public ConvertProcessingException(String message) {
        super(CONVERT_PROCESSING_ERROR + message);
    }
}
