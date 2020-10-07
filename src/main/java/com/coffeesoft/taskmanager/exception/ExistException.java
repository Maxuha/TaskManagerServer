package com.coffeesoft.taskmanager.exception;

public class ExistException extends TaskManagerException {
    public ExistException(String message) {
        super(message);
    }

    public ExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
