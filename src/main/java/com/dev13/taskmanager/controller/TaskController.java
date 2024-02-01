package com.dev13.taskmanager.controller;

import com.dev13.taskmanager.controller.request.TaskRequest;
import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.data.DateRange;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.Task;
import com.dev13.taskmanager.entity.dto.TaskDto;
import com.dev13.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping("/{id}")
    public CustomResponse<TaskDto> getTaskById(@PathVariable Long id) {
        return service.getTaskById(id);
    }

    @GetMapping("/list")
    public CustomResponse<List<TaskDto>> getAllUserTasks(Principal principal) {
        String name = principal.getName();
        return service.getAllUserTasks(name);
    }

    @GetMapping("/list/active")
    public CustomResponse<List<TaskDto>> getAllActiveUserTasks(Principal principal) {
        String name = principal.getName();
        return service.getAllActiveUserTask(name);
    }

    @GetMapping("/list/{dateRange}")
    public CustomResponse<List<TaskDto>> getAllUserTasksByDateRange
            (Principal principal, @PathVariable String dateRange) {
        String name = principal.getName();

        try {
            DateRange range = DateRange.valueOf(dateRange.toUpperCase(Locale.ENGLISH));
            return service.getAllUserTasksByDateRange(name, range);
        } catch (IllegalArgumentException e) {
            return CustomResponse.failed(Error.INVALID_DATE_RANGE);
        }
    }

    @GetMapping("/list/active/{dateRange}")
    public CustomResponse<List<TaskDto>> getAllUserActiveTasksByDateRange
            (Principal principal, @PathVariable String dateRange) {
        String name = principal.getName();

        try {
            DateRange range = DateRange.valueOf(dateRange.toUpperCase(Locale.ENGLISH));
            return service.getAllUserActiveTasksByDateRange(name, range);
        } catch (IllegalArgumentException e) {
            return CustomResponse.failed(Error.INVALID_DATE_RANGE);
        }
    }

    @PostMapping("/create")
    public CustomResponse<TaskDto> create(Principal principal, @RequestBody TaskRequest request) {
        String name = principal.getName();
        return service.create(name, request.getDescription(), request.getDate());
    }

    @PostMapping("/edit/{id}")
    public CustomResponse<TaskDto> edit
            (@PathVariable Long id, @RequestBody TaskRequest request) {
        return service.editTask(id, request.getDescription(), request.getDate());
    }

    @PostMapping("/delete/{id}")
    public CustomResponse<Task> delete(@PathVariable Long id) {
        return service.deleteTask(id);
    }
}
