package com.loanflow.exception;

import org.springframework.http.HttpStatus;

public class LoanLimitExceededException extends ApplicationException {

    public LoanLimitExceededException(String message) {
        super(message, "LOAN_LIMIT_EXCEEDED", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}