package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.ExistException;
import com.coffeesoft.taskmanager.exception.NotExistException;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

@RestController
@RequestMapping(path = "/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public Collection<User> users() {
        logger.info("Request to get all users");
        return userService.getUsers();
    }

    @GetMapping(path = "/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        logger.info("Request to get user with id: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = ResponseEntity.ok().body(userService.getUserById(id));
            logger.info(String.format("Received user with id %d from DB", id));
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn(e.getMessage());
        }
        return responseEntity;
    }

    @PostMapping(path = "/user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createUser(@Validated @RequestBody User user) throws URISyntaxException {
        user.setId(null);
        logger.info("Request to create user: {}", user);
        ResponseEntity<?> responseEntity;
        try {
            User userDb = userService.createUser(user);
            responseEntity = ResponseEntity.created(getUri(userDb.getId())).body(userDb);
            logger.info("Created user: {}", userDb);
        } catch (ExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            logger.warn("Failed to create: " + e.getMessage());
        }
        return responseEntity;
    }

    private URI getUri(Long id) throws URISyntaxException {
        return new URI(String.format("/api/user/%d", id));
    }

    @PutMapping(path = "/user/{id}")
    public ResponseEntity<?> updateUser(@Validated @RequestBody User user, @PathVariable Long  id) {
        logger.info("Request to update user: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            User userDB = userService.getUserById(id);
            userDB.setPassword(user.getPassword());
            userDB.setFullName(user.getFullName());
            userDB.setTasks(user.getTasks());
            userDB = userService.updateUser(userDB);
            responseEntity = ResponseEntity.ok().body(userDB);
            logger.info("Updated user with id {} from {} to {}", id, user, userDB);
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to update: " + e.getMessage());
        }
        return responseEntity;
    }

    @DeleteMapping(path = "/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Request to delete user: {}", id);
        ResponseEntity<?> responseEntity;
        try {
            User user = userService.getUserById(id);
            userService.deleteUserById(user.getId());
            String message = String.format("Deleted user with id %d", id);
            responseEntity = ResponseEntity.ok().body(message);
            logger.info(message);
        } catch (NotExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            logger.warn("Failed to delete: " + e.getMessage());
        }
        return responseEntity;
    }
}
