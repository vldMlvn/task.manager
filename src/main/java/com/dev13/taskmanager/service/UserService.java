package com.dev13.taskmanager.service;

import com.dev13.taskmanager.data.CustomResponse;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private static final Byte MAX_PASSWORD_LENGTH = 20;
    private static final Byte MIN_PASSWORD_LENGTH = 8;
    private static final Byte MAX_USERNAME_LENGTH = 20;
    private static final Byte MIN_USERNAME_LENGTH = 4;

    public CustomResponse<User> create
            (final String username, final String email, final String password) {

        if (!isValidUsername(username)) {
            return CustomResponse.failed(Error.INVALID_USERNAME);
        }

        if (repository.findByUsername(username).isPresent()) {
            return CustomResponse.failed(Error.USERNAME_ALREADY_EXIST);
        }

        if (repository.findByEmail(email).isPresent()) {
            return CustomResponse.failed(Error.EMAIL_ALREADY_EXIST);
        }

        if (isValidPassword(password)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .build();

            repository.save(user);
            return CustomResponse.noBodySuccess();

        } else {
            return CustomResponse.failed(Error.INVALID_PASSWORD);
        }
    }

    private boolean isValidUsername(String username) {
          return username.length() <= MAX_USERNAME_LENGTH &&
                username.length() >= MIN_USERNAME_LENGTH;
    }

    private boolean isValidPassword(String password){
        return password.length() <= MAX_PASSWORD_LENGTH &&
                password.length() >= MIN_PASSWORD_LENGTH;
    }
}

