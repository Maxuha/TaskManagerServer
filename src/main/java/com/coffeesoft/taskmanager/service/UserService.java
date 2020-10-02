package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.UserExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    User getUserById(Long id) throws UserNotExistException;
    User createUser(User user) throws UserExistException;
    User updateUser(User user);
    void deleteUserById(Long id);
}
