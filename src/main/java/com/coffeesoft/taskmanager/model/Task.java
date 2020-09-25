package com.coffeesoft.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "task_seq")
    @SequenceGenerator(name = "task_seq",
                        sequenceName = "SEQ_TASK", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "title", nullable = false, length = 64)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
