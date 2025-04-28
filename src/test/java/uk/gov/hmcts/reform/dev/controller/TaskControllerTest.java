package uk.gov.hmcts.reform.dev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
            .title("Test Task")
            .description("Test Description")
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();
    }

    @Test
    void createTask_ValidInput_ReturnsCreated() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getAllTasks_ReturnsTaskList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(taskDto));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTaskById_ExistingId_ReturnsTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskDto);

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTask_ValidInput_ReturnsUpdatedTask() throws Exception {
        when(taskService.updateTask(Mockito.eq(1L), any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(put("/api/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void deleteTask_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void updateTaskStatus_ValidStatus_ReturnsUpdatedTask() throws Exception {
        when(taskService.updateTaskStatus(Mockito.eq(1L), Mockito.eq("COMPLETED"))).thenReturn(taskDto);

        mockMvc.perform(patch("/api/tasks/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"COMPLETED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OPEN")); // Assuming the mock returns "OPEN"
    }
}
