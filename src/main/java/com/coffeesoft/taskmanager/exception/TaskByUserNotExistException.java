package com.coffeesoft.taskmanager.exception;

public class TaskByUserNotExistException extends NotExistException {
    public TaskByUserNotExistException(Long userId) {
        super(String.format("Current or next task for user with id '%d' not exist", userId));
    }

    public TaskByUserNotExistException(Long userId, Throwable cause) {
        super(String.format("Current or next task for user with id '%d not exist", userId), cause);
    }
}
