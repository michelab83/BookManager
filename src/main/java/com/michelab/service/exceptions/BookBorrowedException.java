package com.michelab.service.exceptions;

public class BookBorrowedException extends RuntimeException {

    public BookBorrowedException(String message) {
        super(message);
    }
}
