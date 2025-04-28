package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskDto taskDto;
    private Task task;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
            .title("Test Task")
            .description("Test Description")
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        task = new Task(
            1L,
            "Test Task",
            "Test Description",
            "OPEN",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        );

    }

    @Test
    void createTask_ValidDto_ReturnsTaskDto() {
        // Arrange
        when(taskRepository.save(any(Task.class)))
            .thenAnswer(invocation -> {
                Task t = invocation.getArgument(0);
                t.setId(1L);
                return t;
            });

        // Act
        TaskDto result = taskService.createTask(taskDto);

        // Assert
        assertThat(result.getTitle()).isEqualTo(taskDto.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasks_WhenTasksExist_ReturnsDtoList() {
        // Arrange
        when(taskRepository.findAll())
            .thenReturn(List.of(task));

        // Act
        List<TaskDto> result = taskService.getAllTasks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(task.getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_ExistingId_ReturnsDto() {
        when(taskRepository.findById(1L))
            .thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(1L);

        assertThat(result.getTitle()).isEqualTo(task.getTitle());
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_NonExistingId_ThrowsResourceNotFoundException() {
        when(taskRepository.findById(999L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Task not found");
        verify(taskRepository).findById(999L);
    }

    @Test
    void updateTask_ExistingId_UpdatesAndReturnsDto() {
        when(taskRepository.findById(1L))
            .thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class)))
            .thenReturn(task);

        TaskDto result = taskService.updateTask(1L, taskDto);

        assertThat(result.getStatus()).isEqualTo(taskDto.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_NonExistingId_ThrowsResourceNotFoundException() {
        when(taskRepository.findById(2L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(2L, taskDto))
            .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository).findById(2L);
    }

    @Test
    void updateTaskStatus_ExistingId_ReturnsDtoWithNewStatus() {
        when(taskRepository.findById(1L))
            .thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class)))
            .thenReturn(task);

        TaskDto result = taskService.updateTaskStatus(1L, "COMPLETED");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskStatus_NonExistingId_ThrowsResourceNotFoundException() {
        when(taskRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTaskStatus(3L, "X"))
            .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository).findById(3L);
    }

    @Test
    void deleteTask_ExistingId_DeletesWithoutException() {
        when(taskRepository.findById(1L))
            .thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_NonExistingId_ThrowsResourceNotFoundException() {
        when(taskRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(4L))
            .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository).findById(4L);
    }

}
