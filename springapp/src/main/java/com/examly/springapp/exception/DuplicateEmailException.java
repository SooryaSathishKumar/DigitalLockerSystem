package com.examly.springapp.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException() {
        super("Email is already registered");
    }
}
