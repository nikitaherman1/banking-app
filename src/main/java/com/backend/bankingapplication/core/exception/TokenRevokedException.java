package com.backend.bankingapplication.core.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenRevokedException extends AuthenticationException {

    public TokenRevokedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenRevokedException(String message) {
        super(message);
    }
}
