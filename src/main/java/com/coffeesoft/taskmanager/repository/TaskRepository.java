package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM task WHERE time > current_timestamp ORDER BY time DESC LIMIT 1",
    nativeQuery = true)
    Task findNextOrCurrentTaskByUserId(Long userId);
}
