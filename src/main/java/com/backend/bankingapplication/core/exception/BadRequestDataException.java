package com.backend.bankingapplication.core.exception;

public class BadRequestDataException  extends RuntimeException {

    public BadRequestDataException(String message) {
        super(message);
    }
}
