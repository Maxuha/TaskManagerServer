package com.coffeesoft.taskmanager.controller;

import com.coffeesoft.taskmanager.exception.ExistException;
import com.coffeesoft.taskmanager.exception.UserNotExistException;
import com.coffeesoft.taskmanager.model.AuthenticationRequest;
import com.coffeesoft.taskmanager.model.Role;
import com.coffeesoft.taskmanager.model.Status;
import com.coffeesoft.taskmanager.model.User;
import com.coffeesoft.taskmanager.security.JwtTokenProvider;
import com.coffeesoft.taskmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthenticationRestController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationRestController.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationRestController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userService.getUserByUsername(request.getUsername());
            String token = jwtTokenProvider.createToken(request.getUsername(), user.getRole().name());
            Map<Object, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            logger.info("Success authenticate user {}", user);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException | UserNotExistException e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<>("Invalid username/password combination", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        logger.info("Request to register user: {}", user);
        ResponseEntity<?> responseEntity;
        try {
            User userDb = userService.createUser(user);
            responseEntity = ResponseEntity.ok().body(userDb);
            logger.info("Created user: {}", userDb);
        } catch (ExistException e) {
            responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            logger.warn("Failed to create: " + e.getMessage());
        }
        return responseEntity;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
        logger.info(String.valueOf(request.getHeaders("Authorization")));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        logger.info("Request to get profile for user {}", username);
        try {
            User user = userService.getUserByUsername(username);
            logger.info("Success get authorized user {}", user);
            return ResponseEntity.ok().body(user);
        } catch (UsernameNotFoundException | UserNotExistException e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
    }
}
