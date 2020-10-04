package com.coffeesoft.taskmanager.exception;

public class TaskExistException extends ExistException {
    public TaskExistException(EntityField entityField) {
        super(String.format("Task with %s '%s' already exist", entityField.getName(), entityField.getId()));
    }

    public TaskExistException(EntityField entityField, Throwable cause) {
        super(String.format("Task with %s '%s' already exist", entityField.getName(), entityField.getId()), cause);
    }
}
