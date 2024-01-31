package com.dev13.taskmanager.service;

import com.dev13.taskmanager.data.CustomResponse;
import com.dev13.taskmanager.data.DateRange;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.Task;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.TaskDto;
import com.dev13.taskmanager.repository.TaskRepository;
import com.dev13.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private final UserRepository userRepository;

    public CustomResponse<TaskDto> create
            (String username, String description, LocalDateTime date) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            Task task = Task.builder()
                    .user(user.get())
                    .description(description)
                    .date(date)
                    .createDate(LocalDateTime.now())
                    .isActive(true)
                    .build();

            TaskDto taskDto = convertToDto(task);
            return CustomResponse.success(taskDto);
        } else {
            return CustomResponse.failed(Error.USER_NOT_FOUND);
        }
    }

    public CustomResponse<TaskDto> getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            TaskDto taskDto = convertToDto(task.get());
            return CustomResponse.success(taskDto);
        } else {
            return CustomResponse.failed(Error.TASK_NOT_FOUND);
        }
    }

    public CustomResponse<List<TaskDto>> getAllUserTasks(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            List<Task> tasks = getUserTasks(user.get());

            if (tasks.isEmpty()) {
                return CustomResponse.failed(Error.TASKS_NOT_FOUND);
            } else {
                List<TaskDto> taskDtoList = convertListToDto(tasks);
                return CustomResponse.success(taskDtoList);
            }
        } else {
            return CustomResponse.failed(Error.USER_NOT_FOUND);
        }
    }

    public CustomResponse<List<TaskDto>> getAllActiveUserTask(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            List<Task> tasks = getUserTasks(user.get());

            List<Task> activeTasks = tasks.stream()
                    .filter(Task::isActive)
                    .toList();

            if (activeTasks.isEmpty()) {
                return CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);
            } else {
                List<TaskDto> taskDtoList = convertListToDto(activeTasks);
                return CustomResponse.success(taskDtoList);
            }
        } else {
            return CustomResponse.failed(Error.USER_NOT_FOUND);
        }
    }

    public CustomResponse<List<TaskDto>> getAllUserTasksByDateRange(String username, DateRange dateRange) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            List<Task> tasks = getUserTasks(user.get());
            Predicate<Task> dateFilter = getDateFilter(dateRange);

            if (dateFilter == null) {
                return CustomResponse.failed(Error.INVALID_DATE_RANGE);
            }

            List<Task> filteredTasks = tasks.stream()
                    .filter(dateFilter)
                    .sorted()
                    .toList();

            if (filteredTasks.isEmpty()) {
                return CustomResponse.failed(Error.TASKS_NOT_FOUND);
            } else {
                List<TaskDto> taskDtoList = convertListToDto(filteredTasks);
                return CustomResponse.success(taskDtoList);
            }
        } else {
            return CustomResponse.failed(Error.USER_NOT_FOUND);
        }
    }

    public CustomResponse<List<TaskDto>> getAllUserActiveTasksByDateRange(String username, DateRange dateRange) {
        List<TaskDto> tasks = getAllUserTasksByDateRange(username, dateRange).getBody();

        List<TaskDto> filteredTasks = tasks.stream()
                .filter(TaskDto::isActive)
                .toList();

        if (filteredTasks.isEmpty()) {
            return CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);
        } else {
            return CustomResponse.success(filteredTasks);
        }
    }

    public CustomResponse<TaskDto> editTask(Long taskId, String newDescription, LocalDateTime newDate) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            task.setDescription(newDescription);
            task.setDate(newDate);

            Task updatedTask = taskRepository.save(task);
            TaskDto taskDto = convertToDto(updatedTask);

            return CustomResponse.success(taskDto);
        } else {
            return CustomResponse.failed(Error.TASK_NOT_FOUND);
        }
    }

    @Transactional
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

    private TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .username(task.getUser().getUsername())
                .description(task.getDescription())
                .date(task.getDate())
                .createDate(task.getCreateDate())
                .isActive(task.isActive())
                .build();
    }

    private List<TaskDto> convertListToDto(List<Task> tasks) {
        return tasks.stream()
                .map(this::convertToDto)
                .toList();
    }
}
