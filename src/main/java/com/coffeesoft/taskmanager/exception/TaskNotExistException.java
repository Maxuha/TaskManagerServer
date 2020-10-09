package com.coffeesoft.taskmanager.exception;

import com.coffeesoft.taskmanager.model.EntityField;

public class TaskNotExistException extends NotExistException {
    public TaskNotExistException(EntityField entityField) {
        super(String.format("Task with %s '%s' not exist", entityField.getName(), entityField.getId()));
    }

    public TaskNotExistException(EntityField entityField, Throwable cause) {
        super(String.format("Task with %s '%s' not exist", entityField.getName(), entityField.getId()), cause);
    }
}
