package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.NotExistException;
import com.coffeesoft.taskmanager.exception.TaskByUserNotExistException;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

@RestController
@RequestMapping(path = "/api")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(path = "/tasks")
    public Collection<Task> tasks() {
        logger.info("Request to get all tasks");
        return taskService.getTasks();
    }


    @GetMapping(path = "/user/{userId}/tasks")
    public Collection<Task> getTasksByUserId(@PathVariable Long userId) {
        logger.info("Request to get user's all tasks");
        return taskService.getTasksByUserId(userId);
    }

    @GetMapping(path = "/task/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        logger.info("Request to get task with id: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = ResponseEntity.ok().body(taskService.getTaskById(id));
            logger.info(String.format("Received task with id %d from DB", id));
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping(path = "/user/{userId}/task/{after}")
    public ResponseEntity<?> getNextOrCurrentTaskAfterTimeByUserId(@PathVariable Long userId, @PathVariable Long after) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(after * 1000), ZoneId.systemDefault());
        logger.info("Request to get next or current task after time {} by user id: {}", time, userId);
        ResponseEntity<?> responseEntity;
        try {
            Task task = taskService.getNextOrCurrentTaskAfterTimeByUserId(time, userId);
            responseEntity = ResponseEntity.ok().body(task);
            logger.info(String.format("Received task with id %d from DB", task.getId()));
        } catch (TaskByUserNotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }
        return responseEntity;
    }

    @PostMapping(path = "/task")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createTask(@Validated @RequestBody Task task) throws URISyntaxException {
        logger.info("Request to create task: {}", task);
        Task taskDb = taskService.createTask(task);
        ResponseEntity<?> responseEntity = ResponseEntity.created(getUri(taskDb.getId())).body(taskDb);
        logger.info("Created task: {}", taskDb);
        return responseEntity;
    }

    private URI getUri(Long id) throws URISyntaxException {
        return new URI(String.format("/api/task/%d", id));
    }

    @PutMapping(path = "/task/{id}")
    public ResponseEntity<?> updateTask(@Validated @RequestBody Task task, @PathVariable Long  id) {
        logger.info("Request to update task: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            Task taskDB = taskService.getTaskById(id);
            taskDB.setTitle(task.getTitle());
            taskDB.setDescription(task.getDescription());
            taskDB.setTime(task.getTime());
            taskDB.setStartTime(task.getStartTime());
            taskDB.setEndTime(task.getEndTime());
            taskDB.setSleepInterval(task.getSleepInterval());
            taskDB.setWorkInterval(task.getWorkInterval());
            taskDB.setActive(task.getActive());
            taskDB.setRepeat(task.getRepeat());
            taskDB.setSleep(task.getSleep());
            taskDB = taskService.updateTask(taskDB);
            responseEntity = ResponseEntity.ok().body(taskDB);
            logger.info("Updated task with id {} from {} to {}", id, task, taskDB);
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to update: " + e.getMessage());
        }
        return responseEntity;
    }

    @DeleteMapping(path = "/task/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Request to delete task: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            Task task = taskService.getTaskById(id);
            taskService.deleteTaskById(task.getId());
            String message = String.format("Deleted task with id %d", id);
            responseEntity = ResponseEntity.ok().body(message);
            logger.info(message);
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to delete: " + e.getMessage());
        }
        return responseEntity;
    }
}
