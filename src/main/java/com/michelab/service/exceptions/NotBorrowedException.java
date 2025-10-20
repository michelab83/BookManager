package com.michelab.service.exceptions;

public class NotBorrowedException extends RuntimeException {
    public NotBorrowedException(String message) {
        super(message);
    }
}
