package com.coffeesoft.taskmanager.service;

import com.coffeesoft.taskmanager.exception.NotExistException;
import com.coffeesoft.taskmanager.exception.TaskByUserNotExistException;
import com.coffeesoft.taskmanager.exception.TaskNotExistException;
import com.coffeesoft.taskmanager.model.EntityField;
import com.coffeesoft.taskmanager.model.Task;
import com.coffeesoft.taskmanager.model.TaskState;
import com.coffeesoft.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public Task getTaskById(Long id) throws TaskNotExistException {
        final String fieldName = "id";
        return taskRepository.findById(id).orElseThrow(() ->
                new TaskNotExistException(new EntityField(id.toString(), fieldName)));
    }

    @Override
    public Task getTaskByTaskIdAndUserId(Long taskId, Long userId) throws TaskByUserNotExistException {
        return taskRepository.findByIdAndUserId(taskId, userId).orElseThrow(() ->
                new TaskByUserNotExistException(userId));
    }

    @Override
    public Task getNextOrCurrentTaskAfterTimeByUserId(LocalDateTime time, Long userId) throws TaskByUserNotExistException {
        return taskRepository.findNextOrCurrentTaskAfterTimeByUserId(time, userId).orElseThrow(() ->
                new TaskByUserNotExistException(userId));
    }

    @Override
    public Task createTask(Task task) {
        task.setTime(task.getStartTime());
        task.setTaskState(TaskState.DISABLE);
        if (task.getWorkInterval() == null) {
            task.setWorkInterval((int) (task.getEndTime().toEpochSecond(ZoneOffset.UTC) -
                    task.getStartTime().toEpochSecond(ZoneOffset.UTC)));
        }
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
