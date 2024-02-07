package com.dev13.taskmanager.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.when;

@SpringBootTest
class JwtUtilTest {

    @Mock
    private UserDetailsService service;
    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void testGenerateToken() {

        //Given
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("Test111")
                .password("test1111")
                .build();
        when(service.loadUserByUsername(userDetails.getUsername())).thenReturn(userDetails);

        //When
        String token = jwtUtil.generateToken(userDetails.getUsername());

        //Then
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    void testValidateTokenWithValidToken() {

        //Given
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("Test111")
                .password("test1111")
                .build();

        when(service.loadUserByUsername(userDetails.getUsername())).thenReturn(userDetails);

        String token = jwtUtil.generateToken(userDetails.getUsername());

        //When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        //Then
        Assertions.assertTrue(isValid);
    }
}
