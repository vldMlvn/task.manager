package com.dev13.taskmanager.security;

import com.dev13.taskmanager.entity.User;
import com.dev13.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService service;
    @Mock
    private UserRepository repository;

    @Test
    void testLoadUserByUsernameSuccessfully() {

        //Given
        User user = User.builder()
                .username("test111")
                .password("test1111")
                .build();

        when(repository.findByUsername(any())).thenReturn(Optional.of(user));

        UserDetails expect = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getUsername())
                .build();

        //When
        UserDetails result = service.loadUserByUsername(user.getUsername());

        //Then
        Assertions.assertEquals(expect, result);
    }

    @Test
    void testLoadUserByUsernameWithUserNotFound() {

        // Given
        when(repository.findByUsername(any())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
    }

}
