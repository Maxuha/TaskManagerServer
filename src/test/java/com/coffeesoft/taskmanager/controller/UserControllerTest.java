package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.UserExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.service.UserService;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserService userServiceMock;

    private User user;

    @BeforeEach
    public void setUp() throws UserExistException {
        user = new User();
        user.setFullName("Ivan Ivanov");
        user.setUsername("ivan_ivanov");
        user.setPassword("parolyaNet0");
        user.setTasks(new HashSet<>());

        user = userService.createUser(user);
        userServiceMock = Mockito.mock(UserService.class);
    }

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void users() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);

        Mockito.when(userServiceMock.getUsers()).thenReturn(users);

        final String url = "/api/users";

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(users);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    public void getUserExist() throws Exception {
        final Long id = user.getId();

        Mockito.when(userServiceMock.getUserById(id)).thenReturn(user);

        final String url = String.format("/api/user/%d", id);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = objectMapper.writeValueAsString(user);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    public void getUserNotExist() throws Exception {
        final Long id = user.getId() + 1;
        final String url = String.format("/api/user/%d", id);

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isNotFound()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = String.format("User with id '%d' not exist", id);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
    }

    @Test
    public void createUserIfNotExist() throws Exception {
        final String username = "ivan_ivanov2";
        final String fullName = "Ivan Ivanov2";
        final String password = "parolyaNet1";
        Set<Task> tasks = new HashSet<>();
        User newUser = new User(null, fullName, username, password, tasks);
        User createdUser = new User(user.getId() + 1, fullName, username, password, tasks);

        Mockito.when(userServiceMock.createUser(newUser)).thenReturn(createdUser);

        final String url = "/api/user";
        mockMvc.perform(
                post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(newUser))
                ).andExpect(status().isCreated())
                .andExpect(content().string("Created user: " + createdUser))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void createUserIfExist() throws Exception {
        final String username = "ivan_ivanov";
        final String fullName = "Ivan Ivanov";
        final String password = "parolyaNet0";
        Set<Task> tasks = new HashSet<>();
        User newUser = new User(null, fullName, username, password, tasks);
        final String url = "/api/user";

        mockMvc.perform(
                post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(newUser))
                ).andExpect(status().isConflict())
                .andExpect(content().string(String.format("User with username '%s' already exist", username)))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void updateUser() throws Exception {
        final String username = "ivan_ivanov";
        final String fullName = "Ivan Ivanov 3";
        final String password = "parolyaNet2";
        final Long id = user.getId();
        Set<Task> tasks = new HashSet<>();
        User newUser = new User(id, fullName, username, password, tasks);

        Mockito.when(userServiceMock.updateUser(newUser)).thenReturn(newUser);

        final String url = String.format("/api/user/%d", newUser.getId());
        mockMvc.perform(
                put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(newUser))
                ).andExpect(status().isOk())
                .andExpect(content().string(String.format("Updated user with id %d from %s to %s", user.getId(), user, newUser)))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void deleteUser() throws Exception {
        final Long id = user.getId();
        final String url = String.format("/api/user/%d", id);

        mockMvc.perform(delete(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        Assertions.assertThrows(UserNotExistException.class, () -> userService.getUserById(id));
    }
}