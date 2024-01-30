package com.dev13.taskmanager.service;

import com.dev13.taskmanager.data.CustomResponse;
import com.dev13.taskmanager.data.DateRange;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.Task;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public CustomResponse<Task> create
            (User user, String description, LocalDateTime date) {

        Task task = Task.builder()
                .user(user)
                .description(description)
                .date(date)
                .build();
        return CustomResponse.success(task);
    }

    public CustomResponse<List<Task>> getAllUserTasks(User user) {
        List<Task> tasks = getUserTasks(user);

        if (!tasks.isEmpty()) {
            return CustomResponse.success(tasks);
        } else {
            return CustomResponse.failed(Error.TASKS_NOT_FOUND);
        }
    }

    public CustomResponse<List<Task>> getAllActiveUserTask(User user) {
        List<Task> tasks = getUserTasks(user);

        List<Task> activeTasks = tasks.stream()
                .filter(Task::isActive)
                .toList();

        if (!activeTasks.isEmpty()) {
            return CustomResponse.success(activeTasks);
        } else {
            return CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);
        }
    }

    public CustomResponse<List<Task>> getAllUserTasksByDateRange(User user, DateRange dateRange) {
        List<Task> tasks = getUserTasks(user);
         Predicate<Task> dateFilter = getDateFilter(dateRange);

        if(dateFilter == null){
            return CustomResponse.failed(Error.INVALID_DATE_RANGE);
        }

        List<Task> filteredTasks = tasks.stream()
                .filter(dateFilter)
                .sorted()
                .toList();

        if (!filteredTasks.isEmpty()) {
            return CustomResponse.success(filteredTasks);
        } else {
            return CustomResponse.failed(Error.TASKS_NOT_FOUND);
        }
    }

    public CustomResponse<List<Task>> getAllUserActiveTasksByDateRange(User user, DateRange dateRange) {
        List<Task> tasks = getAllUserTasksByDateRange(user, dateRange).getDto();

        List<Task> filteredTasks = tasks.stream()
                .filter(Task::isActive)
                .toList();

        if (!filteredTasks.isEmpty()) {
            return CustomResponse.success(filteredTasks);
        } else {
            return CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);
        }
    }

    public CustomResponse<Task> editTask(Long taskId, String newDescription, LocalDateTime newDate) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            task.setDescription(newDescription);
            task.setDate(newDate);

            Task updatedTask = taskRepository.save(task);

            return CustomResponse.success(updatedTask);
        } else {
            return CustomResponse.failed(Error.TASK_NOT_FOUND);
        }
    }

    public CustomResponse<Task> deleteTask(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            taskRepository.delete(task.get());
            return CustomResponse.noBodySuccess();
        } else {
            return CustomResponse.failed(Error.TASK_NOT_FOUND);
        }

    }

    private List<Task> getUserTasks(User user) {
        return taskRepository.findAllByUserId(user.getId());
    }

    private Predicate<Task> getDateFilter(DateRange dateRange) {
        LocalDate now = LocalDate.now();

        return switch (dateRange) {
            case DAY -> task -> task.getDate().toLocalDate().equals(now);
            case WEEK -> task -> task.getDate().toLocalDate().isAfter(now.minusDays(1)) &&
                    task.getDate().toLocalDate().isBefore(now.plusWeeks(1));
            case MONTH -> task -> task.getDate().toLocalDate().isAfter(now.minusDays(1)) &&
                    task.getDate().toLocalDate().isBefore(now.plusMonths(1));
            default -> null;
        };
    }
}
