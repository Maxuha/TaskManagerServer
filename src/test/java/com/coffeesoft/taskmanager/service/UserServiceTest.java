package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.UserExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setFullName("Ivan Ivanov");
        user.setUsername("ivan_ivanov");
        user.setPassword("parolyaNet0");
        user.setTasks(new HashSet<>());

        userRepository.save(user);
    }

    @Test
    public void contextLoads() {
        assertThat(userService).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void getUsersCount() {
        final Integer size = 1;
        List<User> users = userService.getUsers();

        assertThat(users).size().isEqualTo(size);
    }

    @Test
    public void getUsersContent() {
        List<User> users = userService.getUsers();

        assertThat(users.get(0)).isEqualTo(user);
    }

    @Test
    public void getUserByIdExist() throws UserNotExistException {
        final Long id = user.getId();

        User userFromDb = userService.getUserById(id);

        assertThat(userFromDb).isEqualTo(user);
    }

    @Test(expected = UserNotExistException.class)
    public void getUserByIdNotExist() throws UserNotExistException {
        final Long id = user.getId() + 1;

        User userFromDb = userService.getUserById(id);

        assertThat(userFromDb).isNull();
    }

    @Test
    public void createUserIfNotExist() throws UserExistException {
        User user = new User();
        user.setFullName("Ivan Ivanov2");
        user.setUsername("ivan_ivanov2");
        user.setPassword("parolyaNet1");

        User userCreated = userService.createUser(user);
        user.setId(userCreated.getId());

        assertThat(userCreated).isEqualTo(user);
    }

    @Test(expected = UserExistException.class)
    public void createUserIfExist() throws UserExistException {
        User user = new User();
        user.setFullName("Ivan Ivanov");
        user.setUsername("ivan_ivanov");
        user.setPassword("parolyaNet0");

        User userCreated = userService.createUser(user);

        assertThat(userCreated).isNull();
    }

    @Test
    public void updateUser() {
        final Long id = user.getId();
        User user = new User();
        user.setId(id);
        user.setFullName("Ivan Ivanov3");
        user.setUsername("ivan_ivanov");
        user.setPassword("parolyaNet2");

        User updatedUser = userService.updateUser(user);

        assertThat(updatedUser).isEqualTo(user);
    }

    @Test(expected = UserNotExistException.class)
    public void deleteUser() throws UserNotExistException {
        final Long id = user.getId();

        userService.getUserById(id);

        userService.deleteUserById(id);

        User deleteUser = userService.getUserById(id);

        assertThat(deleteUser).isNull();
    }
}