package com.coffeesoft.taskmanager.model;

import com.coffeesoft.taskmanager.annotation.validator.PasswordConstraint;
import com.coffeesoft.taskmanager.annotation.validator.UsernameConstraint;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "user_seq")
    @SequenceGenerator(name = "user_seq",
                        sequenceName = "SEQ_USER", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;
    @Column(name = "full_name", nullable = false, length = 36)
    private String fullName;
    @UsernameConstraint
    @Column(name = "username", nullable = false, updatable = false, unique = true)
    private String username;
    @PasswordConstraint
    @Column(name = "password", nullable = false)
    private String password;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Task> tasks;
}
