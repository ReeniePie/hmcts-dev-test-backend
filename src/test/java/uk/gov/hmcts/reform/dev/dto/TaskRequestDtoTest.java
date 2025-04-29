package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskRequestDtoTest
{
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validateCreateTaskDto_AllFieldsValid_NoViolations() {
        // Arrange
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title("Valid Title")
            .description("A description under 500 chars")
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validateTitle_NullTitle_ThrowsNotNullViolation() {
        // Arrange
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title(null)                 // violates @NotNull
            .description("Desc")
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .extracting("message")
            .containsExactly("Title must not be null");
    }

    @Test
    void validateTitle_ShortTitle_ThrowsSizeViolation() {
        // Arrange
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title("Hi")                 // shorter than min=3
            .description("Desc")
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations)
            .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                && v.getMessage().contains("between 3 and 100"));
    }

    @Test
    void validateDescription_LongDescription_ThrowsSizeViolation() {
        // Arrange
        String longDesc = "x".repeat(501); // >500 chars
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title("Valid")
            .description(longDesc)
            .status("OPEN")
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations)
            .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                && v.getMessage().contains("less than 500"));
    }

    @Test
    void validateStatus_LongStatus_ThrowsSizeViolation() {
        // Arrange
        String longStatus = "S".repeat(21); // >20 chars
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title("Valid")
            .description("Desc")
            .status(longStatus)
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations)
            .anyMatch(v -> v.getPropertyPath().toString().equals("status")
                && v.getMessage().contains("less than 20"));
    }

    @Test
    void validateDueDate_PastDate_ThrowsFutureViolation() {
        // Arrange
        TaskRequestDto taskRequestDto = TaskRequestDto.builder()
            .title("Valid")
            .description("Desc")
            .status("OPEN")
            .dueDate(LocalDateTime.now().minusDays(1))  // violates @Future
            .build();

        // Act
        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(taskRequestDto);

        // Assert
        assertThat(violations)
            .anyMatch(v -> v.getPropertyPath().toString().equals("dueDate")
                && v.getMessage().contains("must be in the future"));
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }
}
