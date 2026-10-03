package com.examly.springapp.exception;

public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException() {
        super("File size exceeds the maximum allowed limit of 50 MB");
    }
}
