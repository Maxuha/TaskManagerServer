package com.coffeesoft.taskmanager.model;

import com.coffeesoft.taskmanager.annotation.validator.PasswordConstraint;
import com.coffeesoft.taskmanager.annotation.validator.UsernameConstraint;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(exclude="tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
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
    @NonNull
    @Column(name = "full_name", nullable = false, length = 36)
    private String fullName;
    @NonNull
    @UsernameConstraint
    @Column(name = "username", nullable = false, updatable = false, unique = true)
    private String username;
    @NonNull
    @PasswordConstraint
    @Column(name = "password", nullable = false)
    private String password;
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Task> tasks;
}
