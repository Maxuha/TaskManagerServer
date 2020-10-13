package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.TaskState;
import com.coffeesoft.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;

    private Task task;

    @BeforeEach
    public void setUp() {
        final String title = "Teeth cleaning";
        final LocalDateTime startTime = LocalDateTime.now(Clock.systemUTC()).plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;

        user = new User("Ivan Ivanov", "ivan_ivanov", "parolyaNet0");

        task = new Task(title, startTime, endTime, active, repeat, sleep, user);
        task.setTime(startTime);
        task.setWorkInterval((int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)));
        task.setTaskState(TaskState.DISABLE);

        entityManager.persist(task);
        entityManager.flush();
    }

    @Test
    public void contextLoads() {
        assertThat(entityManager).isNotNull();
        assertThat(taskRepository).isNotNull();
    }

    @Test
    public void findAllCount() {
        final Integer size = 1;
        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).size().isEqualTo(size);
    }

    @Test
    public void findAllContent() {
        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks.get(0)).isEqualTo(task);
    }

    @Test
    public void findByIdExist() {
        final Long id = task.getId();

        Optional<Task> taskFromDb = taskRepository.findById(id);

        assertThat(taskFromDb).isPresent();
        assertThat(taskFromDb.get().getTitle()).isEqualTo(task.getTitle());
    }

    @Test
    public void findByIdNotExist() {
        final Long id = task.getId() + 1;

        Optional<Task> taskFromDb = taskRepository.findById(id);

        assertThat(taskFromDb).isNotPresent();
    }

    @Test
    public void findAllContentByUserId() {
        final Long userId = user.getId();
        List<Task> tasks = taskRepository.findByUserId(userId);

        assertThat(tasks.get(0)).isEqualTo(task);
    }

    @Test
    public void findNextOrCurrentTaskAfterTimeByUserIdExist() {
        final Long userId = user.getId();

        Optional<Task> taskFromDb = taskRepository
                .findNextOrCurrentTaskAfterTimeByUserId(LocalDateTime.now(Clock.systemUTC()), userId);

        assertThat(taskFromDb).isPresent();
        assertThat(taskFromDb.get().getTitle()).isEqualTo(task.getTitle());
    }

    @Test
    public void findNextOrCurrentTaskAfterTimeByUserIdNotExist() {
        final Long userId = user.getId();

        Optional<Task> taskFromDb = taskRepository
                .findNextOrCurrentTaskAfterTimeByUserId(LocalDateTime.now(Clock.systemUTC()).plusMinutes(2), userId);

        assertThat(taskFromDb).isNotPresent();
    }

    @Test
    public void save() {
        final String title = "To wash the dishes";
        final LocalDateTime startTime = task.getEndTime().plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;

        Task task = new Task(title, startTime, endTime, active, repeat, sleep, user);
        task.setTime(startTime);
        task.setWorkInterval((int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)));
        task.setTaskState(TaskState.DISABLE);

        Task savedTask = taskRepository.save(task);
        task.setId(savedTask.getId());

        assertThat(savedTask).isEqualTo(task);
    }

    @Test
    public void update() {
        final Long id = task.getId();
        final String title = "Go for bread";
        final String description = "Go to the store for bread";
        final LocalDateTime startTime = task.getEndTime().plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;
        final long sleepInterval = 0L;
        final long workInterval = startTime.toEpochSecond(ZoneOffset.UTC) - endTime.toEpochSecond(ZoneOffset.UTC);

        Task task = new Task(id, title, description, startTime, startTime, endTime, (int) sleepInterval,
                (int) workInterval, active, repeat, sleep, TaskState.DISABLE, user);

        Task updatedTask = taskRepository.save(task);

        assertThat(updatedTask).isEqualTo(task);
    }

    @Test
    public void deleteById() {
        final Long id = task.getId();

        Optional<Task> taskFromDb = taskRepository.findById(id);

        taskRepository.deleteById(id);

        Optional<Task> deletedTask = taskRepository.findById(id);

        assertThat(taskFromDb).isPresent();
        assertThat(deletedTask).isNotPresent();
    }
}