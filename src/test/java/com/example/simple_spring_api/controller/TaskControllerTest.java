package com.example.simple_spring_api.controller;

import com.example.simple_spring_api.dto.TaskDto;
import com.example.simple_spring_api.exceptions.TaskNotFoundException;
import com.example.simple_spring_api.model.Task;
import com.example.simple_spring_api.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .completed(false)
                .build();
    }

    // ---------------- Positive Tests ----------------

    @Test
    void testCreateTask() throws Exception {
        TaskDto dto = new TaskDto(1L, "Test Task", "Test Description", false);

        Mockito.when(taskService.saveTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.data.name").value("Test Task"));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDto dto = new TaskDto(1L, "Updated Task", "Updated Description", false);
        Task updatedTask = Task.builder()
                .id(1L)
                .name("Updated Task")
                .description("Updated Description")
                .completed(false)
                .build();

        Mockito.when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Task"));
    }

    @Test
    void testGetTaskById() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task fetched successfully"))
                .andExpect(jsonPath("$.data.name").value("Test Task"));
    }

    @Test
    void testGetAllTasks() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task fetched successfully"))
                .andExpect(jsonPath("$.data[0].name").value("Test Task"));
    }

    // ---------------- Validation Tests ----------------

    @Test
    void testCreateTaskValidationFails_BlankTitle() throws Exception {
        TaskDto dto = new TaskDto(1L, "", "Valid description", false);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTaskValidationFails_BlankTitle() throws Exception {
        TaskDto dto = new TaskDto(1L, "   ", "Some description", false);

        mockMvc.perform(put("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTaskValidationFails_TitleTooLong() throws Exception {
        String longTitle = "a".repeat(101); // longer than 100 chars
        TaskDto dto = new TaskDto(1L, longTitle, "Valid description", false);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ---------------- Exception Tests ----------------

    @Test
    void testUpdateTaskNotFound() throws Exception {
        TaskDto dto = new TaskDto(999L, "Nonexistent", "Does not exist", false);

        Mockito.when(taskService.updateTask(any(Task.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        Mockito.when(taskService.getTaskById(42L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/tasks/42"))
                .andExpect(status().isNotFound());
    }
}
