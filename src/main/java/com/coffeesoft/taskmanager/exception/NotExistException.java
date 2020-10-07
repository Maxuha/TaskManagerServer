package com.coffeesoft.taskmanager.exception;

public abstract class NotExistException extends TaskManagerException {
    public NotExistException(String message) {
        super(message);
    }
    public NotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
