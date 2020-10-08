package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.TaskByUserNotExistException;
import com.coffeesoft.taskmanager.exception.TaskNotExistException;
import com.coffeesoft.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    List<Task> getTasks();
    List<Task> getTasksByUserId(Long userId);
    Task getTaskById(Long id) throws TaskNotExistException;
    Task getNextOrCurrentTaskAfterTimeByUserId(LocalDateTime time, Long userId) throws TaskByUserNotExistException;
    Task createTask(Task task);
    Task update(Task task);
    void deleteTaskById(Long id);
}
