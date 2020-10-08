package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.TaskByUserNotExistException;
import com.coffeesoft.taskmanager.exception.TaskNotExistException;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
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

        taskRepository.save(task);
    }

    @Test
    public void contextLoads() {
        assertThat(taskService).isNotNull();
        assertThat(taskRepository).isNotNull();
    }

    @Test
    void getTasksCount() {
        final Integer size = 1;
        List<Task> tasks = taskService.getTasks();

        assertThat(tasks).size().isEqualTo(size);
    }

    @Test
    void getTasksContent() {
        List<Task> tasks = taskService.getTasks();

        assertThat(tasks.get(0)).isEqualTo(task);
    }

    @Test
    void getTasksByUserId() {
        final Long userId = user.getId();
        List<Task> tasks = taskService.getTasksByUserId(userId);

        assertThat(tasks.get(0)).isEqualTo(task);
    }

    @Test
    void getTaskByIdExist() throws TaskNotExistException {
        final Long id = task.getId();

        Task taskFromDb = taskService.getTaskById(id);

        assertThat(taskFromDb).isEqualTo(task);
    }

    @Test
    void getTaskByIdNotExist() {
        final Long id = task.getId() + 1;
        Assertions.assertThrows(TaskNotExistException.class, () -> taskService.getTaskById(id));
    }

    @Test
    void getNextOrCurrentTaskAfterTimeByUserIdExist() throws TaskByUserNotExistException {
        final Long userId = user.getId();

        Task taskFromDb = taskService.getNextOrCurrentTaskAfterTimeByUserId(LocalDateTime.now(Clock.systemUTC()), userId);

        assertThat(taskFromDb).isEqualTo(task);
    }

    @Test
    void getNextOrCurrentTaskAfterTimeByUserIdNotExist() {
        final Long userId = user.getId();

        Assertions.assertThrows(TaskByUserNotExistException.class, () ->
                taskService.getNextOrCurrentTaskAfterTimeByUserId(LocalDateTime.now(Clock.systemUTC()).plusMinutes(2), userId));
    }

    @Test
    void createTaskIfNotExist() {
        final String title = "To wash the dishes";
        final LocalDateTime startTime = task.getEndTime().plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;

        Task task = new Task(title, startTime, endTime, active, repeat, sleep, user);
        task.setTime(startTime);
        task.setWorkInterval((int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)));

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isEqualTo(task);
    }

    @Test
    void update() {
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
                (int) workInterval, active, repeat, sleep, user);

        Task updatedTask = taskService.update(task);

        assertThat(updatedTask).isEqualTo(task);
    }

    @Test
    void deleteTaskById() throws TaskNotExistException {
        final Long id = task.getId();

        taskService.getTaskById(id);

        taskService.deleteTaskById(id);

        Assertions.assertThrows(TaskNotExistException.class, () -> taskService.getTaskById(id));
    }
}