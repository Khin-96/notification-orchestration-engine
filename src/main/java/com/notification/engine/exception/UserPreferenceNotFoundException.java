package com.notification.engine.exception;

public class UserPreferenceNotFoundException extends RuntimeException {
    public UserPreferenceNotFoundException(String message) {
        super(message);
    }
}
