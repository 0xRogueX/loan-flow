package com.loanflow.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends ApplicationException {

    public InvalidStatusTransitionException(String message) {
        super(message, "INVALID_STATUS_TRANSITION", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}