package com.dev13.taskmanager.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService service;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private JwtRequestFilter filter;


    @Test
    void testDoFilterInternal() throws ServletException, IOException {
        // Given

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("Test111")
                .password("test1111")
                .build();

        when(jwtUtil.extractUsername(anyString())).thenReturn("Test111");
        when(jwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(service.loadUserByUsername("Test111")).thenReturn(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer your_token_here");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        verify(filterChain).doFilter(request, response);
    }
}
