package com.coffeesoft.taskmanager.repository;

import com.coffeesoft.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
