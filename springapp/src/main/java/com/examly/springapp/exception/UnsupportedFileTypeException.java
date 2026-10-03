package com.examly.springapp.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }

    public UnsupportedFileTypeException() {
        super("Unsupported file type. Only PDF, DOCX, JPEG/JPG, PNG, and TXT are supported.");
    }
}
