package com.examly.springapp.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException() {
        super("Document not found");
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }
}