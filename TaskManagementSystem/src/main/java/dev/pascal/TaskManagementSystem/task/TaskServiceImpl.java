package dev.pascal.TaskManagementSystem.task;

import dev.pascal.TaskManagementSystem.user.User;
import dev.pascal.TaskManagementSystem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO, User user) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setDeadline(taskDTO.getDeadline());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return mapToDTO(savedTask);
    }




    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {

        Task task = taskRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Task not found"));
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setDeadline(taskDTO.getDeadline());

        //findout if the user exists

        if(taskDTO.getUserId() != null){
            User user =userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(()->new RuntimeException("User not found"));
            task.setUser(user);
        }
        Task updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask) ;
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public List<TaskDTO> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByStatus(Long userId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId,status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTaskByPriority(Long userId, TaskPriority priority) {
        return taskRepository.findByUserIdAndPriority(userId,priority)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(task);
    }

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDeadline(task.getDeadline());

        // let's get user id

        if (task.getUser() != null){
            dto.setUserId(task.getUser().getId());
        }
        return dto;
    }
}
