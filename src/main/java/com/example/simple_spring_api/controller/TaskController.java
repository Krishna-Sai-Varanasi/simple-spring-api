package com.example.simple_spring_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple_spring_api.dto.ResponseDto;
import com.example.simple_spring_api.dto.TaskDto;
import com.example.simple_spring_api.exceptions.TaskNotFoundException;
import com.example.simple_spring_api.model.Task;
import com.example.simple_spring_api.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    // This class will handle HTTP requests related to tasks
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Return new instance of Task from TaskDto
     * 
     * @param taskDto
     * @return Task
     */
    private Task convertToTask(TaskDto taskDto) {
        return Task.builder()
                .name(taskDto.title())
                .description(taskDto.description())
                .completed(false)
                .build();
    }

    /**
     * Create a new task
     * 
     * This method handles POST requests to create a new task.
     * 
     * @param taskDto the data transfer object containing task details
     * @return ResponseEntity with a ResponseDto containing the created task
     *         and a status of CREATED (201)
     * 
     * @Valid annotation ensures that the taskDto is validated before processing.
     */
    @PostMapping
    public ResponseEntity<ResponseDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        Task createdTask = taskService.saveTask(convertToTask(taskDto));
        ResponseDto response = new ResponseDto(
                HttpStatus.CREATED,
                "Task created successfully",
                createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing task
     * 
     * This method handles PUT requests to update an existing task.
     * 
     * @param taskDto the data transfer object containing updated task details
     * @return ResponseEntity with a ResponseDto containing the updated task
     *         and a status of OK (200)
     * @throws TaskNotFoundException
     * @Valid annotation ensures that the taskDto is validated before processing.
     */
    @PutMapping
    public ResponseEntity<ResponseDto> updateTask(@Valid @RequestBody TaskDto taskDto) {
        Task updatedTask = taskService.updateTask(convertToTask(taskDto));
        if (updatedTask == null) {
            throw new TaskNotFoundException("Task not found with ID: " + taskDto.id());
        }
        ResponseDto response = new ResponseDto(
                HttpStatus.OK,
                "Task updated successfully",
                updatedTask);
        return ResponseEntity.ok(response);
    }

    /**
     * Find task by id
     * 
     * This method handles the GET requests to fetch an existing task
     * 
     * @param taskId the id of the task that needs to be collected from the backend
     * @return ResponseEntity with a ResponseDto containing the task
     *         and a status of OK (200) other wise a TaskNotfoundException will be
     *         thrown
     * @throws TaskNotFoundException
     */

    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseDto> getTaskById(@PathVariable long taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Task not found with ID: " + taskId);
        }
        ResponseDto response = new ResponseDto(
                HttpStatus.OK,
                "Task fetched successfully",
                task);
        return ResponseEntity.ok(response);
    }

    /**
     * Find all tasks
     * 
     * This method handles the GET request to fetch all tasks
     * 
     * @return ResponseEntity with a ResponseDto containing the list of tasks from
     *         the backend and a status of OK (200) otherwise NO_CONTENT
     * 
     */
    @GetMapping
    public ResponseEntity<ResponseDto> getAllTasks() {
        var tasks = taskService.getAllTasks();
        ResponseDto response = null;
        if (tasks.isEmpty()) {
            response = new ResponseDto(
                    HttpStatus.NO_CONTENT,
                    "No tasks found",
                    tasks);
        }
        response = new ResponseDto(
                HttpStatus.OK,
                "Task fetched successfully",
                tasks);
        return ResponseEntity.ok(response);
    }

    

    

}
