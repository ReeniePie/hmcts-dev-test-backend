package uk.gov.hmcts.reform.dev.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.Optional;
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

    public TaskDto createTask(TaskDto taskDto) {
        Task task = modelMapper.map(taskDto, Task.class);
        return modelMapper.map(taskRepository.save(task), TaskDto.class);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return modelMapper.map( task, TaskDto.class);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        modelMapper.map(taskDto, task);
        return modelMapper.map(taskRepository.save(task), TaskDto.class);
    }

    public TaskDto updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        task.setStatus(status);
        return modelMapper.map(taskRepository.save(task), TaskDto.class);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        taskRepository.delete(task);
    }

}
