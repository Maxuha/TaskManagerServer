package com.coffeesoft.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "task_seq")
    @SequenceGenerator(name = "task_seq",
                        sequenceName = "SEQ_TASK", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NonNull
    @Column(name = "title", nullable = false, length = 64)
    private String title;
    @Column(name = "description")
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "time", nullable = false)
    private LocalDateTime time;
    @NonNull
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    @NonNull
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    @Column(name = "sleep_interval")
    private Integer sleepInterval;
    @Column(name = "work_interval", nullable = false)
    private Integer workInterval;
    @NonNull
    @Column(name = "active", nullable = false)
    private Boolean active;
    @NonNull
    @Column(name = "repeat", nullable = false)
    private Boolean repeat;
    @NonNull
    @Column(name = "sleep", nullable = false)
    private Boolean sleep;
    @NonNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "task_state", nullable = false)
    private TaskState taskState;
    @JsonBackReference
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setTaskStateAndNextTimeAfter(LocalDateTime current) {
        if (!active) {
            taskState = TaskState.DISABLE;
        }

        if (time == null) {
            time = startTime;
        }

        if (!time.isAfter(current)) {
            if (taskState == TaskState.DISABLE) {
                taskState = TaskState.START;
            } else if (taskState == TaskState.START) {
                time = time.plusMinutes(workInterval);
                taskState = TaskState.WORK;
            } else if (taskState == TaskState.WORK) {
                if (sleep) {
                    time = time.plusMinutes(sleepInterval);
                    taskState = TaskState.SLEEP;
                }
            } else if (taskState == TaskState.SLEEP) {
                taskState = TaskState.START;
            }
        }
    }
}