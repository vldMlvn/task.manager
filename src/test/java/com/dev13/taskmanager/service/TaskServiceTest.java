package com.dev13.taskmanager.service;

import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.data.DateRange;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.Task;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.TaskDto;
import com.dev13.taskmanager.repository.TaskRepository;
import com.dev13.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
 class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TaskService service;

    @Test
    void testCreateSuccessfully() {

        //Given
        String username = "test";
        String description = "test description";
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .username(username)
                .build();

        Task task = Task.builder()
                .user(user)
                .date(now)
                .description(description)
                .isActive(true)
                .build();


        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));


        //When
        CustomResponse<TaskDto> result = service.create(username, description, now);
        LocalDateTime createDate = result.getBody().getCreateDate();
        task.setCreateDate(createDate);
        CustomResponse<TaskDto> expect = CustomResponse.success(service.convertToDto(task));


        //Then
        assertEquals(expect, result);
    }

    @Test
    void testCreateWithNotFoundUser() {

        //Given
        String username = "test";
        String description = "test description";
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<TaskDto> result = service.create(username, description, now);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetByIdSuccessfully() {

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .build();

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));

        CustomResponse<TaskDto> expect = CustomResponse.success(service.convertToDto(task));

        //When
        CustomResponse<TaskDto> result = service.getTaskById(task.getId());

        //Then
        assertEquals(expect, result);

    }

    @Test
    void testGetTaskByIdWithNotFoundTask() {

        //Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        CustomResponse<Object> expect = CustomResponse.failed(Error.TASK_NOT_FOUND);

        //When
        CustomResponse<TaskDto> result = service.getTaskById(id);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetAllUserTasksSuccessfully() {
        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .build();

        List<Task> taskList = Collections.singletonList(task);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);

        CustomResponse<List<TaskDto>> expect = CustomResponse.success(service.convertListToDto(taskList));

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks(user.getUsername());

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetAllUserTasksWithTasksNotFound() {

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByUserId(any(Long.class))).thenReturn(Collections.emptyList());

        CustomResponse<Object> expect = CustomResponse.failed(Error.TASKS_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks("test");

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetAllUserTasksWithUserNotFound() {

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks("test");

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetAllActiveUserTaskSuccessfully() {

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();

        LocalDateTime data = LocalDateTime.now().plusDays(1);

        Task task = Task.builder()
                .id(1L)
                .user(user)
                .date(data)
                .isActive(true)
                .build();

        List<Task> taskList = Collections.singletonList(task);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);

        CustomResponse<List<TaskDto>> expect = CustomResponse.success(service.convertListToDto(taskList));

        //When
        CustomResponse<List<TaskDto>> result = service.getAllActiveUserTask(user.getUsername());

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testGetAllUserActiveTasksWithActiveTasksNotFound() {

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByUserId(any(Long.class))).thenReturn(Collections.emptyList());

        CustomResponse<Object> expect = CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllActiveUserTask("test");

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllActiveUserTasksWithUserNotFound(){

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllActiveUserTask("test");

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksByDateRangeSuccessfully(){

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();

        LocalDateTime data = LocalDateTime.now();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .date(data)
                .isActive(true)
                .build();

        List<Task> taskList = Collections.singletonList(task);

        DateRange dateRangeDay = DateRange.DAY;
        DateRange dateRangeWeek = DateRange.WEEK;
        DateRange dateRangeMonth = DateRange.MONTH;

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);
        CustomResponse<List<TaskDto>> expect = CustomResponse.success(service.convertListToDto(taskList));


        //When
        CustomResponse<List<TaskDto>> resultDay =
                service.getAllUserTasksByDateRange(user.getUsername(), dateRangeDay);
        CustomResponse<List<TaskDto>> resultWeek =
                service.getAllUserTasksByDateRange(user.getUsername(), dateRangeWeek);
        CustomResponse<List<TaskDto>> resultMonth =
                service.getAllUserTasksByDateRange(user.getUsername(), dateRangeMonth);

        //When
        assertEquals(expect, resultDay);
        assertEquals(expect,resultWeek);
        assertEquals(expect,resultMonth);
    }

    @Test
    void testGetAllUserTasksByDateRangeWithInvalidDateRange(){

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByUserId(any(Long.class))).thenReturn(new ArrayList<>());

        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_DATE_RANGE);

        //When
        CustomResponse<List<TaskDto>> result = service
                .getAllUserTasksByDateRange("test", DateRange.INVALID_DATE);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksByDateRangeWithTasksNotFound(){

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByUserId(any())).thenReturn(Collections.emptyList());

        CustomResponse<Object> expect = CustomResponse.failed(Error.TASKS_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasksByDateRange("test", DateRange.DAY);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksByDateRangeWithUserNotFound(){

        //Given
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(taskRepository.findAllByUserId(any())).thenReturn(new ArrayList<>());

        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasksByDateRange("test", DateRange.DAY);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserActiveTasksByDateRangeSuccessfully(){

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();

        LocalDateTime data = LocalDateTime.now();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .date(data)
                .isActive(true)
                .build();

        List<Task> taskList = Collections.singletonList(task);

        DateRange dateRangeDay = DateRange.DAY;
        DateRange dateRangeWeek = DateRange.WEEK;
        DateRange dateRangeMonth = DateRange.MONTH;

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);
        CustomResponse<List<TaskDto>> expect = CustomResponse.success(service.convertListToDto(taskList));

        //When
        CustomResponse<List<TaskDto>> resultDay =
                service.getAllUserActiveTasksByDateRange(user.getUsername(), dateRangeDay);
        CustomResponse<List<TaskDto>> resultWeek =
                service.getAllUserActiveTasksByDateRange(user.getUsername(), dateRangeWeek);
        CustomResponse<List<TaskDto>> resultMonth =
                service.getAllUserActiveTasksByDateRange(user.getUsername(), dateRangeMonth);

        //Then
        assertEquals(expect,resultDay);
        assertEquals(expect,resultWeek);
        assertEquals(expect,resultMonth);
    }

    @Test
    void testGetAllUserActiveTasksByDateRangeWithActiveTasksNotFound(){

        //Given
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByUserId(any(Long.class))).thenReturn(Collections.emptyList());

        CustomResponse<Object> expect = CustomResponse.failed(Error.ACTIVE_TASKS_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service
                .getAllUserActiveTasksByDateRange("test", DateRange.DAY);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testEditTaskSuccessfully(){

        //Given
        Task task = Task.builder()
                .id(1L)
                .user(new User())
                .description("test description")
                .date(LocalDateTime.now())
                .build();

        String newDescription = "new description";
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        Task editedTask = Task.builder()
                .id(task.getId())
                .user(task.getUser())
                .description(newDescription)
                .date(date)
                .build();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(editedTask);

        CustomResponse<TaskDto> expect = CustomResponse.success(service.convertToDto(task));

        //When
        CustomResponse<TaskDto> result = service.editTask(task.getId(), newDescription, date);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testEditTaskWithTaskNotFound(){

        //Given
        String newDescription = "new description";
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        CustomResponse<Object> expect = CustomResponse.failed(Error.TASK_NOT_FOUND);

        //When
        CustomResponse<TaskDto> result = service.editTask(1L, newDescription, date);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testDeleteTaskSuccessfully(){

        //Given
        Task task = Task.builder()
                .id(1L)
                .user(new User())
                .description("test description")
                .date(LocalDateTime.now())
                .build();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        CustomResponse<Object> expect = CustomResponse.noBodySuccess();

        //When
        CustomResponse<Task> result = service.deleteTask(task.getId());

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testDeleteTaskWithTaskNotFound(){

        //Given
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        CustomResponse<Object> expect = CustomResponse.failed(Error.TASK_NOT_FOUND);

        //When
        CustomResponse<Task> result = service.deleteTask(1L);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testUpdateStatus(){

        //Given
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        Task task = Task.builder()
                .id(1L)
                .date(date)
                .isActive(true)
                .build();

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        Task expected = Task.builder()
                .id(task.getId())
                .date(task.getDate())
                .isActive(false)
                .build();

        //When
        service.updateTaskStatus();

        //Then
        assertEquals(expected,task);
    }
}