package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.TaskNotExistException;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.TaskState;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    private TaskService taskServiceMock;

    private Task task;

    private User user;

    @BeforeEach
    void setUp() {
        final String title = "Teeth cleaning";
        final LocalDateTime startTime = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(2);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;
        user = new User("Ivan Ivanov", "ivan_ivanov", "parolyaNet0");

        task = new Task(title, startTime, endTime, active, repeat, sleep, user);

        task = taskService.createTask(task);
        taskServiceMock = Mockito.mock(TaskService.class);
    }


    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(taskService).isNotNull();
    }

    @Test
    void tasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        Mockito.when(taskServiceMock.getTasks()).thenReturn(tasks);

        final String url = "/api/tasks";

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(tasks);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    void getTaskExist() throws Exception {
        final Long id = task.getId();

        Mockito.when(taskServiceMock.getTaskById(id)).thenReturn(task);

        final String url = String.format("/api/task/%d", id);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(task);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    void getTaskNotExist() throws Exception {
        final Long id = task.getId() + 1;
        final String url = String.format("/api/task/%d", id);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isNotFound()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = String.format("Task with id '%d' not exist", id);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    void getTasksByUserId() throws Exception {
        final Long userId = user.getId();
        final String url = String.format("/api/user/%d/tasks", userId);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(tasks);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    void getNextOrCurrentTaskAfterTimeByUserId() throws Exception {
        final Long userId = user.getId();

        Mockito.when(taskServiceMock.getNextOrCurrentTaskAfterTimeByUserId(LocalDateTime.now(ZoneOffset.UTC), userId)).thenReturn(task);

        final String url = String.format("/api/user/%d/task/%d", userId, LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) - 10800);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(task);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    void createTask() throws Exception {
        final String title = "Breakfast";
        final LocalDateTime startTime = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;

        Task newTask = new Task(title, startTime, endTime, active, repeat, sleep, user);
        Task createdTask = new Task(title, startTime, endTime, active, repeat, sleep, user);
        createdTask.setId(task.getId() + 1);
        createdTask.setTime(startTime);
        createdTask.setWorkInterval((int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)));
        createdTask.setTaskState(TaskState.DISABLE);

        Mockito.when(taskServiceMock.createTask(newTask)).thenReturn(createdTask);

        final String url = "/api/task";
        mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newTask))
        ).andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(createdTask)))
                .andDo(print())
                .andReturn();
    }

    @Test
    void updateTask() throws Exception {
        final Long id = task.getId();
        final String title = "Go for bread";
        final String description = "Go to the store for bread";
        final LocalDateTime startTime = task.getEndTime().plusMinutes(1);
        final LocalDateTime endTime = startTime.plusMinutes(2);
        final boolean active = true;
        final boolean repeat = false;
        final boolean sleep = false;
        final int sleepInterval = 0;
        final int workInterval = (int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC));

        Task newTask = new Task(id, title, description, null, startTime, endTime,
                sleepInterval, workInterval, active, repeat, sleep, TaskState.DISABLE, user);
        Task updatedTask = new Task(id, title, description, null, startTime, endTime,
                sleepInterval, workInterval, active, repeat, sleep, TaskState.DISABLE, user);

        Mockito.when(taskServiceMock.updateTask(newTask)).thenReturn(updatedTask);

        final String url = String.format("/api/task/%d", id);
        mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newTask))
        ).andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(updatedTask)))
                .andDo(print())
                .andReturn();
    }

    @Test
    void deleteUser() throws Exception {
        final Long id = task.getId();
        final String url = String.format("/api/task/%d", id);

        mockMvc.perform(delete(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        Assertions.assertThrows(TaskNotExistException.class, () -> taskService.getTaskById(id));
    }
}