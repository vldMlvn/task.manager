package com.dev13.taskmanager.service;

import com.dev13.taskmanager.controller.request.LoginRequest;
import com.dev13.taskmanager.controller.request.RegisterRequest;
import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.AuthDto;
import com.dev13.taskmanager.entity.dto.UserDto;
import com.dev13.taskmanager.repository.UserRepository;
import com.dev13.taskmanager.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterSuccessfully() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test111111")
                .email("test111@mail.com")
                .build();

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        CustomResponse<UserDto> expect = CustomResponse.success(userService.convertToDto(user));

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testRegisterWithUserExist() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test111111")
                .email("test111@mail.com")
                .build();

        when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

        CustomResponse<Object> expect = CustomResponse.failed(Error.USERNAME_ALREADY_EXIST);

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testRegisterWithInvalidUsername() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test")
                .password("test111111")
                .email("test111@mail.com")
                .build();

        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_USERNAME);

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testRegisterWithInvalidPassword() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test11")
                .email("test111@mail.com")
                .build();

        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_PASSWORD);

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testRegisterWithInvalidEmail() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test11111")
                .email("test111")
                .build();

        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_EMAIL);

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testRegisterWithNullData() {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test11111")
                .email("test111@mail.com")
                .build();

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database connection failed"));
        CustomResponse<Object> expect = CustomResponse.failed(Error.UNKNOWN_ERROR);

        //When
        CustomResponse<UserDto> result = authService.register(request);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testLoginSuccessfully() {

        //Give
        LoginRequest request = LoginRequest.builder()
                .username("Test111")
                .password("test1111")
                .build();

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email("test@mail.com")
                .build();

        AuthDto authDto = AuthDto.builder()
                .user(userService.convertToDto(user))
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        //When
        CustomResponse<AuthDto> result = authService.login(request);
        authDto.setToken(result.getBody().getToken());
        CustomResponse<AuthDto> expect = CustomResponse.success(authDto);

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testLoginWithInvalidUsername() {

        //Given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_USERNAME);

        //When
        CustomResponse<AuthDto> result = authService.login(new LoginRequest());

        //Then
        assertEquals(expect, result);
    }

    @Test
    void testLoginWithInvalidPassword() {

        //Given
        LoginRequest request = LoginRequest.builder()
                .username("Test111")
                .password("test1111")
                .build();
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        CustomResponse<Object> expect = CustomResponse.failed(Error.INVALID_PASSWORD);

        //When
        CustomResponse<AuthDto> result = authService.login(request);

        //Then
        assertEquals(expect, result);
    }
}
