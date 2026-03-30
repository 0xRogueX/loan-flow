package com.loanflow.exception;

import org.springframework.http.HttpStatus;

public class CreditBureauException extends ApplicationException {

    public CreditBureauException(String message) {
        super(message, "CREDIT_BUREAU_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
    }
}