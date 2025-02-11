package dev.pascal.TaskManagementSystem.task;
import dev.pascal.TaskManagementSystem.security.CustomUserDetails;
import dev.pascal.TaskManagementSystem.user.User;
import dev.pascal.TaskManagementSystem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/tasks")

public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;


    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserDetails principal
            ){
        if (principal == null){throw new RuntimeException("User is not authenticated.");}
        //
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(()-> new RuntimeException("user not found"));

        return ResponseEntity.ok(taskService.createTask(taskDTO, user));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,
                                              @RequestBody TaskDTO taskDTO,
                                              @AuthenticationPrincipal CustomUserDetails principal) {
        TaskDTO existingTask = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (!principal.getId().equals(existingTask.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        TaskDTO task = taskService.getTaskById(id); // Get task details

        if (!task.getUserId().equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not the owner
        }

        taskService.deleteTask(id); // Delete the task
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getUserTasks(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails principal) {
        if(!principal.getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }


    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable Long userId,
                                                          @PathVariable TaskStatus status,
                                                          @AuthenticationPrincipal CustomUserDetails principal){

        if (!principal.getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(taskService.getTasksByStatus(userId,status));
    }

    @GetMapping("/user/{userId}/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTaskByPriority(
            @PathVariable Long userId,
            @PathVariable TaskPriority priority, @AuthenticationPrincipal CustomUserDetails principal) {
        if (!principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(taskService.getTaskByPriority(userId, priority));
    }
}