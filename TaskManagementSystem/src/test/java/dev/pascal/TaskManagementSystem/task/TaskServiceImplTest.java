package dev.pascal.TaskManagementSystem.task;
import dev.pascal.TaskManagementSystem.user.User;
import dev.pascal.TaskManagementSystem.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDTO taskDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Task description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setUser(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Task description");
        taskDTO.setPriority(TaskPriority.HIGH);
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);
        taskDTO.setUserId(1L);
    }

    @Test
    void createTask_ShouldReturnTaskDTO() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.createTask(taskDTO, user);

        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTaskDTO() {
        // Mock the task repository
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Mock userRepository to return a User when findById is called
        if (taskDTO.getUserId() != null) {
            when(userRepository.findById(taskDTO.getUserId()))
                    .thenReturn(Optional.of(new User())); // Return a dummy user
        }

        // Perform the update operation
        TaskDTO updatedTask = taskService.updateTask(1L, taskDTO);

        // Assertions
        assertNotNull(updatedTask);
        assertEquals("Test Task", updatedTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void getTasksByUser_ShouldReturnTaskList() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));

        List<TaskDTO> tasks = taskService.getTasksByUser(1L);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getTaskById_ShouldReturnTaskDTO() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        verify(taskRepository, times(1)).findById(1L);
    }
}
