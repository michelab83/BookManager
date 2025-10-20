package com.michelab.service.exceptions;

public class AllowedLoansExceededException extends RuntimeException {

    public AllowedLoansExceededException(String message) {
        super(message);
    }
}
