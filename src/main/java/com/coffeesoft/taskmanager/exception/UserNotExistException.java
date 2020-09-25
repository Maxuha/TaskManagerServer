package com.coffeesoft.taskmanager.exception;

import com.coffeesoft.taskmanager.model.EntityField;

public class UserNotExistException extends NotExistException {
    public UserNotExistException(EntityField entityField) {
        super(String.format("User with %s '%s' not exist", entityField.getName(), entityField.getId()));
    }

    public UserNotExistException(EntityField entityField, Throwable cause) {
        super(String.format("User with %s '%s' not exist", entityField.getName(), entityField.getId()), cause);
    }
}
