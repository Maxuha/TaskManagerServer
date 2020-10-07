package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM task WHERE time > ?1 AND user_id = ?2 ORDER BY time DESC LIMIT 1",
    nativeQuery = true)
    Optional<Task> findNextOrCurrentTaskAfterTimeByUserId(LocalDateTime time, Long userId);

    List<Task> findByUserId(Long userId);
}
