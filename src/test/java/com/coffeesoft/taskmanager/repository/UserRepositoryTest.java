package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.EntityField;
import com.coffeesoft.taskmanager.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest

public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setFullName("Ivan Ivanov");
        user.setUsername("ivan_ivanov");
        user.setPassword("parolyaNet0");

        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void contextLoads() {
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void findAllCount() {
        final Integer size = 1;
        List<User> users = userRepository.findAll();

        assertThat(users).size().isEqualTo(size);
    }

    @Test
    public void findAllContent() {
        List<User> users = userRepository.findAll();

        assertEquals(user, users.get(0));
    }

    @Test
    public void findByUsernameExist() {
        final String username = user.getUsername();

        Optional<User> userFromDb = userRepository.findByUsername(username);

        assertTrue(userFromDb.isPresent());
        assertThat(userFromDb.get().getId()).isEqualTo(user.getId());
    }

    @Test(expected = UserNotExistException.class)
    public void findByUsernameNotExist() throws UserNotExistException {
        final String username = user.getUsername() + "1";
        Optional<User> userFromDb = userRepository.findByUsername(username);

        assertThat(userFromDb.orElseThrow(() -> new UserNotExistException(new EntityField(username, "username"))));
    }

    @Test
    public void findByIdExist() {
        final Long id = user.getId();

        Optional<User> userFromDb = userRepository.findById(id);

        assertTrue(userFromDb.isPresent());
        assertThat(userFromDb.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test(expected = UserNotExistException.class)
    public void findByIdNotExist() throws UserNotExistException {
        final Long id = user.getId() + 1;

        Optional<User> userFromDb = userRepository.findById(id);

        assertThat(userFromDb.orElseThrow(() -> new UserNotExistException(new EntityField(id.toString(), "id"))));
    }

    @Test
    public void save() {
        User user = new User();
        user.setUsername("ivan_ivanov2");
        user.setFullName("Ivan Ivanov2");
        user.setPassword("parolyaNet1");

        User savedUser = userRepository.save(user);
        user.setId(savedUser.getId());

        assertEquals(user, savedUser);
    }

    @Test
    public void update() {
        final Long id = user.getId();
        User user = new User();
        user.setId(id);
        user.setUsername("ivan_ivanov");
        user.setFullName("Ivan Ivanov3");
        user.setPassword("parolyaNet2");

        User updatedUser = userRepository.save(user);

        assertEquals(user, updatedUser);
    }

    @Test
    public void deleteById() {
        final Long id = user.getId();

        Optional<User> userFromDb = userRepository.findById(id);

        userRepository.deleteById(id);

        Optional<User> deletedUser = userRepository.findById(id);

        assertNotEquals(userFromDb.isPresent(), deletedUser.isPresent());
    }
}