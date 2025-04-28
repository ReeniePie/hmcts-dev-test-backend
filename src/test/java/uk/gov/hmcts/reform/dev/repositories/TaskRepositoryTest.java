package uk.gov.hmcts.reform.dev.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.dev.config.JpaAuditingConfig;
import uk.gov.hmcts.reform.dev.models.Task;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Save and retrieve a Task")
    void saveTask_ValidTask_ReturnsSavedTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Testing save and findById");
        task.setStatus("OPEN");
        task.setDueDate(LocalDateTime.now().plusDays(1));

        // Act
        Task savedTask = taskRepository.save(task);
        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());

        // Assert
        assertThat(retrievedTask).isPresent();
        assertThat(retrievedTask.get().getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Update a Task")
    void updateTask_AllFieldsModified_UpdatesSuccessfully() {
        // Arrange
        Task task = new Task();
        task.setTitle("Initial Title");
        task.setDescription("Initial Description");
        task.setStatus("Pending");
        task.setDueDate(LocalDateTime.now().plusDays(7));
        Task savedTask = taskRepository.save(task);

        // Act
        savedTask.setTitle("Updated Title");
        savedTask.setDescription("Updated Description");
        savedTask.setStatus("Completed");
        LocalDateTime updatedDueDate = LocalDateTime.now().plusDays(14);
        savedTask.setDueDate(updatedDueDate);
        Task updatedTask = taskRepository.save(savedTask);

        // Assert
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTask.getStatus()).isEqualTo("Completed");
        assertThat(updatedTask.getDueDate()).isEqualTo(updatedDueDate);
    }

    @Test
    @DisplayName("Delete a Task")
    void testDeleteTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Delete Test");
        task.setDescription("Testing delete");
        task.setStatus("OPEN");
        task.setDueDate(LocalDateTime.now().plusDays(1));

        Task savedTask = taskRepository.save(task);
        Long taskId = savedTask.getId();

        // Act
        taskRepository.deleteById(taskId);
        Optional<Task> deletedTask = taskRepository.findById(taskId);

        // Assert
        assertThat(deletedTask).isNotPresent();
    }
}
