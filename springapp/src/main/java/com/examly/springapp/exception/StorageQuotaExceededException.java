package com.examly.springapp.exception;

public class StorageQuotaExceededException extends RuntimeException {
    public StorageQuotaExceededException(String message) {
        super(message);
    }

    public StorageQuotaExceededException() {
        super("User storage quota exceeded");
    }
}
