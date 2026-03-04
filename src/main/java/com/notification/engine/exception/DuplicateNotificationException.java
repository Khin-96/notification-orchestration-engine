package com.notification.engine.exception;

public class DuplicateNotificationException extends RuntimeException {
    public DuplicateNotificationException(String message) {
        super(message);
    }
}
