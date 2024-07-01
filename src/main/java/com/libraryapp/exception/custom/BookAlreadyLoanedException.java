package com.libraryapp.exception.custom;

public class BookAlreadyLoanedException extends RuntimeException {

    public BookAlreadyLoanedException(String message) {
        super(message);
    }
}
