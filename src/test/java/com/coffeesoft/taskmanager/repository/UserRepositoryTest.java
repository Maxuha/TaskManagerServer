package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
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

        assertThat(users.get(0)).isEqualTo(user);
    }

    @Test
    public void findByUsernameExist() {
        final String username = user.getUsername();

        Optional<User> userFromDb = userRepository.findByUsername(username);

        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getId()).isEqualTo(user.getId());
    }

    @Test
    public void findByUsernameNotExist() {
        final String username = user.getUsername() + "1";
        Optional<User> userFromDb = userRepository.findByUsername(username);

        assertThat(userFromDb).isNotPresent();
    }

    @Test
    public void findByIdExist() {
        final Long id = user.getId();

        Optional<User> userFromDb = userRepository.findById(id);

        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void findByIdNotExist() {
        final Long id = user.getId() + 1;

        Optional<User> userFromDb = userRepository.findById(id);

        assertThat(userFromDb).isNotPresent();
    }

    @Test
    public void save() {
        User user = new User();
        user.setUsername("ivan_ivanov2");
        user.setFullName("Ivan Ivanov2");
        user.setPassword("parolyaNet1");

        User savedUser = userRepository.save(user);
        user.setId(savedUser.getId());

        assertThat(savedUser).isEqualTo(user);
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

        assertThat(updatedUser).isEqualTo(user);
    }

    @Test
    public void deleteById() {
        final Long id = user.getId();

        Optional<User> userFromDb = userRepository.findById(id);

        userRepository.deleteById(id);

        Optional<User> deletedUser = userRepository.findById(id);

        assertThat(userFromDb).isPresent();
        assertThat(deletedUser).isNotPresent();
    }
}