package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.NotExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.security.JwtTokenProvider;
import com.coffeesoft.taskmanager.service.TaskService;
import com.coffeesoft.taskmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api")
public class TaskRestController {
    private static final Logger logger = LoggerFactory.getLogger(TaskRestController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TaskService taskService;

    public TaskRestController(JwtTokenProvider jwtTokenProvider, UserService userService, TaskService taskService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping(path = "/tasks")
    @PreAuthorize("hasAuthority('tasks:read')")
    public ResponseEntity<?> tasks(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request get all tasks for user {}", username);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserByUsername(username);
            Collection<Task> tasks = taskService.getTasksByUserId(user.getId());
            responseEntity = ResponseEntity.ok().body(tasks);
            logger.info("Received tasks for user id {} from DB", user.getUsername());
        } catch (UserNotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping(path = "/task/{taskId}")
    @PreAuthorize("hasAuthority('tasks:read')")
    public ResponseEntity<?> getTask(@PathVariable Long taskId, @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request get task {} for user {}", taskId, username);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserByUsername(username);
            responseEntity = ResponseEntity.ok().body(taskService.getTaskByTaskIdAndUserId(taskId, user.getId()));
            logger.info("Received task with id {} for user {} from DB", taskId, user.getUsername());
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }
        return responseEntity;
    }

    @PostMapping(path = "/task")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('tasks:write')")
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task, @RequestHeader("Authorization") String token) throws URISyntaxException {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request to create task {} for user {}", task, username);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserByUsername(username);
            task.setUser(user);
            Task taskDb = taskService.createTask(task);
            responseEntity = ResponseEntity.created(getUri(taskDb.getId())).body(taskDb);
            logger.info("Created task: {}", taskDb);
        } catch (UserNotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }

        return responseEntity;
    }

    private URI getUri(Long id) throws URISyntaxException {
        return new URI(String.format("/api/task/%d", id));
    }

    @PutMapping(path = "/task/{taskId}")
    @PreAuthorize("hasAuthority('tasks:write')")
    public ResponseEntity<?> updateTask(@Validated @RequestBody Task task,
                                        @PathVariable Long taskId, @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request to update task {} for user {}", taskId, username);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserByUsername(username);
            Task taskDB = taskService.getTaskById(taskId);
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
            taskDB.setUser(user);
            taskDB = taskService.updateTask(taskDB);
            responseEntity = ResponseEntity.ok().body(taskDB);
            logger.info("Updated task {} for user {}", taskDB, user.getUsername());
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to update: " + e.getMessage());
        }
        return responseEntity;
    }

    @DeleteMapping(path = "/task/{taskId}")
    @PreAuthorize("hasAuthority('tasks:write')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request to delete task {} for user {}", taskId, username);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserByUsername(username);
            Task task = taskService.getTaskByTaskIdAndUserId(taskId, user.getId());
            taskService.deleteTaskById(task.getId());
            String message = String.format("Deleted task with id %d", taskId);
            responseEntity = ResponseEntity.ok().body(message);
            logger.info(message);
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to delete: " + e.getMessage());
        }
        return responseEntity;
    }
}
