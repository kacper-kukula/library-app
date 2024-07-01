package com.libraryapp.exception.custom;

public class UnauthorizedViewException extends RuntimeException {

    public UnauthorizedViewException(String message) {
        super(message);
    }
}
