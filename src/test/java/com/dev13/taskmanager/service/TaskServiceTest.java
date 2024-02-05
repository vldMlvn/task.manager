package com.dev13.taskmanager.service;

import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.Task;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.TaskDto;
import com.dev13.taskmanager.repository.TaskRepository;
import com.dev13.taskmanager.repository.UserRepository;
import org.junit.Before;
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
    void testCreateSuccessfully(){

        //Given
        String username = "test";
        String description = "test description";
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .username(username)
                .build();

        TaskDto taskDto = TaskDto.builder()
                .username(username)
                .date(now)
                .description(description)
                .isActive(true)
                .build();



        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));


        //When
        CustomResponse<TaskDto> result = service.create(username, description, now);
        LocalDateTime createDate = result.getBody().getCreateDate();
        taskDto.setCreateDate(createDate);
        CustomResponse<TaskDto> expect = CustomResponse.success(taskDto);


        //Then
        assertEquals(expect,result);
    }

    @Test
    void testCreateWithNotFoundUser(){

        //Given
        String username = "test";
        String description = "test description";
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<TaskDto> result = service.create(username, description, now);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetByIdSuccessfully(){

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .build();

        TaskDto taskDto = TaskDto.builder()
                .id(task.getId())
                .username(user.getUsername())
                .build();

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));

        CustomResponse<TaskDto> expect = CustomResponse.success(taskDto);

        //When
        CustomResponse<TaskDto> result = service.getTaskById(task.getId());

        //Then
        assertEquals(expect,result);

    }

    @Test
    void testGetTaskByIdWithNotFoundTask(){

        //Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        CustomResponse<Object> expect = CustomResponse.failed(Error.TASK_NOT_FOUND);

        //When
        CustomResponse<TaskDto> result = service.getTaskById(id);

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksSuccessfully(){
        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();
        Task task = Task.builder()
                .id(1L)
                .user(user)
                .build();

        List<Task> taskList = new ArrayList<>();
        taskList.add(task);

        TaskDto taskDto = TaskDto.builder()
                .id(task.getId())
                .username(user.getUsername())
                .build();

        List<TaskDto> taskDtoList = new ArrayList<>();
        taskDtoList.add(taskDto);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);

        CustomResponse<List<TaskDto>> expect = CustomResponse.success(taskDtoList);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks(user.getUsername());

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksWithTasksNotFound(){

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .build();

        List<Task> list = Collections.emptyList();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(list);

        CustomResponse<Object> expect = CustomResponse.failed(Error.TASKS_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks(user.getUsername());

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllUserTasksWithUserNotFound(){

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        CustomResponse<Object> expect = CustomResponse.failed(Error.USER_NOT_FOUND);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllUserTasks("test");

        //Then
        assertEquals(expect,result);
    }

    @Test
    void testGetAllActiveUserTaskSuccessfully(){

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

        TaskDto taskDto = TaskDto.builder()
                .id(task.getId())
                .username(user.getUsername())
                .date(data)
                .isActive(true)
                .build();

        List<TaskDto> taskDtoList = Collections.singletonList(taskDto);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(user.getId())).thenReturn(taskList);

        CustomResponse<List<TaskDto>> expect = CustomResponse.success(taskDtoList);

        //When
        CustomResponse<List<TaskDto>> result = service.getAllActiveUserTask(user.getUsername());

        //Then
        assertEquals(expect,result);

    }
}
