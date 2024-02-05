package com.dev13.taskmanager.service;

import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.entity.dto.UserDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void testConvertToDto(){

        //Given
        User user = User.builder()
                .id(1L)
                .username("test")
                .password("test_password")
                .email("test@mail.com")
                .build();

        UserDto expect = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();

        UserService service = new UserService();

        //When
        UserDto result = service.convertToDto(user);

        //Then
        assertEquals(expect,result);
    }
}

