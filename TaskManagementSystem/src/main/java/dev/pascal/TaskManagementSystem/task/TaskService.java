package dev.pascal.TaskManagementSystem.task;

import dev.pascal.TaskManagementSystem.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {

    TaskDTO createTask(TaskDTO taskDTO, User user);
    TaskDTO updateTask(Long id, TaskDTO taskDTO);
    void deleteTask(Long id);
    List<TaskDTO> getTasksByUser(Long userId);
    List<TaskDTO> getTasksByStatus(Long userId, TaskStatus status);
    List<TaskDTO> getTaskByPriority(Long userId, TaskPriority priority);

    TaskDTO getTaskById(Long id);
}
