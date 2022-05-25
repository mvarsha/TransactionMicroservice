package com.accenture.techtask.controller;

public class InvalidTransactionException extends RuntimeException {
    private final String code;

    public String getCode() {
        return code;
    }

    public InvalidTransactionException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.code = errorResponse.getError();
    }
}
