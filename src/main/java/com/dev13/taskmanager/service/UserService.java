package com.dev13.taskmanager.service;

import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
