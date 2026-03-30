package com.loanflow.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends ApplicationException {

    public UnauthorizedAccessException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.FORBIDDEN);
    }
}