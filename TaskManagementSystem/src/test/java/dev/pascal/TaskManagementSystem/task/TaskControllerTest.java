package dev.pascal.TaskManagementSystem.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pascal.TaskManagementSystem.security.CustomUserDetails;
import dev.pascal.TaskManagementSystem.security.JwtUtil;
import dev.pascal.TaskManagementSystem.user.User;
import dev.pascal.TaskManagementSystem.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import({JwtUtil.class})
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private TaskDTO testTaskDTO;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        // Create CustomUserDetails
        customUserDetails = new CustomUserDetails(testUser);

        // Create test TaskDTO
        testTaskDTO = new TaskDTO();
        testTaskDTO.setId(1L);
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setPriority(TaskPriority.MEDIUM);
        testTaskDTO.setStatus(TaskStatus.IN_PROGRESS);
        testTaskDTO.setDeadline(LocalDateTime.now().plusDays(1));
        testTaskDTO.setUserId(1L);

        // Mock userRepository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }



    @Test
    void updateTask_Forbidden() throws Exception {
        TaskDTO unauthorizedTask = new TaskDTO();
        unauthorizedTask.setUserId(2L); // Different user
        when(taskService.getTaskById(1L)).thenReturn(unauthorizedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksByStatus_Success() throws Exception {
        when(taskService.getTasksByStatus(1L, TaskStatus.IN_PROGRESS))
                .thenReturn(Arrays.asList(testTaskDTO));

        mockMvc.perform(get("/api/tasks/user/1/status/IN_PROGRESS")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTasksByStatus_Forbidden() throws Exception {
        mockMvc.perform(get("/api/tasks/user/2/status/IN_PROGRESS")
                        .with(user(customUserDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksByPriority_Success() throws Exception {
        when(taskService.getTaskByPriority(1L, TaskPriority.MEDIUM))
                .thenReturn(Arrays.asList(testTaskDTO));

        mockMvc.perform(get("/api/tasks/user/1/priority/MEDIUM")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }


    @Test
    void deleteTask_Forbidden() throws Exception {
        TaskDTO unauthorizedTask = new TaskDTO();
        unauthorizedTask.setUserId(2L); // Different user
        when(taskService.getTaskById(1L)).thenReturn(unauthorizedTask);

        mockMvc.perform(delete("/api/tasks/1")
                        .with(user(customUserDetails)))
                .andExpect(status().isForbidden());
    }
}