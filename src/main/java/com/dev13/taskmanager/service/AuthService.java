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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final JwtUtil jwtUtil;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserService service;

    public CustomResponse<UserDto> register(RegisterRequest request) {

        if (Boolean.TRUE.equals(repository.existsByUsername(request.getUsername()))) {
            return CustomResponse.failed(Error.USERNAME_ALREADY_EXIST);
        }

        if (!usernameIsValid(request.getUsername())) {
            return CustomResponse.failed(Error.INVALID_USERNAME);
        }

        if (!passwordIsValid(request.getPassword())) {
            return CustomResponse.failed(Error.INVALID_PASSWORD);
        }

        if (!emailIsValid(request.getEmail())) {
            return CustomResponse.failed(Error.INVALID_EMAIL);
        }

        try {
            User createdUser = createUser(request.getUsername(), request.getPassword(), request.getEmail());
            repository.save(createdUser);
            UserDto userDto = service.convertToDto(createdUser);

            return CustomResponse.success(userDto);
        } catch (Exception e) {
            return CustomResponse.failed(Error.UNKNOWN_ERROR);
        }
    }

    public CustomResponse<AuthDto> login(LoginRequest request) {

        Optional<User> userOptional = repository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return CustomResponse.failed(Error.INVALID_USERNAME);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return CustomResponse.failed(Error.INVALID_PASSWORD);
        }

        UserDto userDto = service.convertToDto(userOptional.get());
        String token = jwtUtil.generateToken(request.getUsername());
        AuthDto authDto = AuthDto.builder()
                .user(userDto)
                .token(token)
                .build();


        return CustomResponse.success(authDto);
    }

    private boolean usernameIsValid(String username) {
        return username != null &&
                username.length() >= MIN_USERNAME_LENGTH &&
                username.length() <= MAX_USERNAME_LENGTH;
    }

    private boolean passwordIsValid(String password) {
        return password != null &&
                password.length() >= MIN_PASSWORD_LENGTH &&
                password.length() <= MAX_PASSWORD_LENGTH;
    }

    private boolean emailIsValid(String email) {
        return email != null &&
                email.matches(EMAIL_REGEX);
    }

    private User createUser(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
    }
}
