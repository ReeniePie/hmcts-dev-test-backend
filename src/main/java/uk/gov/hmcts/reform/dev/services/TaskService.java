package uk.gov.hmcts.reform.dev.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.TaskRequestDto;
import uk.gov.hmcts.reform.dev.dto.TaskResponseDto;
import uk.gov.hmcts.reform.dev.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.modelMapper = new ModelMapper();
    }

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Task task = modelMapper.map(taskRequestDto, Task.class);
        return modelMapper.map(taskRepository.save(task), TaskResponseDto.class);
    }

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> modelMapper.map(task, TaskResponseDto.class))
                .collect(Collectors.toList());
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return modelMapper.map( task, TaskResponseDto.class);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        modelMapper.map(taskRequestDto, task);
        return modelMapper.map(taskRepository.save(task), TaskResponseDto.class);
    }

    public TaskResponseDto updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        task.setStatus(status);
        return modelMapper.map(taskRepository.save(task), TaskResponseDto.class);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        taskRepository.delete(task);
    }

}
