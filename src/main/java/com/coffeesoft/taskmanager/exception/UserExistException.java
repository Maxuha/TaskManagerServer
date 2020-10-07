package com.coffeesoft.taskmanager.exception;

import com.coffeesoft.taskmanager.model.EntityField;

public class UserExistException extends ExistException {
    public UserExistException(EntityField entityField) {
        super(String.format("User with %s '%s' already exist", entityField.getName(), entityField.getId()));
    }

    public UserExistException(EntityField entityField, Throwable cause) {
        super(String.format("User with %s '%s' already exist", entityField.getName(), entityField.getId()), cause);
    }
}
