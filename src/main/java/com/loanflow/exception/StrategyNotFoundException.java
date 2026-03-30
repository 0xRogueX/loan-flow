package com.loanflow.exception;

import org.springframework.http.HttpStatus;

public class StrategyNotFoundException extends ApplicationException {

    public StrategyNotFoundException(String message) {
        super(message, "STRATEGY_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
