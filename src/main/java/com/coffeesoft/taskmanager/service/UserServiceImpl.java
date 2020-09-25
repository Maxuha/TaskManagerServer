package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.UserExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.EntityField;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) throws UserNotExistException {
        final String fieldName = "id";
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotExistException(new EntityField(id.toString(), fieldName)));
    }

    @Override
    public User createUser(User user) throws UserExistException {
        hasUser(user);
        return userRepository.save(user);
    }

    private void hasUser(User user) throws UserExistException {
        Optional<User> userDb = userRepository.findByUsername(user.getUsername());
        if (userDb.isPresent()) {
            throw new UserExistException(new EntityField(user.getUsername(), "username"));
        }
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
