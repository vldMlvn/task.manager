package com.dev13.taskmanager.controller;

import com.dev13.taskmanager.controller.request.LoginRequest;
import com.dev13.taskmanager.controller.request.RegisterRequest;
import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.data.Error;
import com.dev13.taskmanager.entity.dto.AuthDto;
import com.dev13.taskmanager.entity.dto.UserDto;
import com.dev13.taskmanager.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void testRegister() throws Exception {

        //Given
        RegisterRequest request = RegisterRequest.builder()
                .username("Test111")
                .password("test1111")
                .email("test@mail.com")
                .build();

        CustomResponse<UserDto> expect = CustomResponse.success(new UserDto());
        when(authService.register(request)).thenReturn(expect);

        //When
        CustomResponse<UserDto> result = authController.register(request);

        //Then
        assertEquals(expect, result);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Error.OK.getMessage()));
    }

    @Test
    void testLogin() throws Exception {

        //Given
        LoginRequest request = LoginRequest.builder()
                .username("Test111")
                .password("test1111")
                .build();

        CustomResponse<AuthDto> expect = CustomResponse.success(new AuthDto());
        when(authService.login(request)).thenReturn(expect);

        //When
        CustomResponse<AuthDto> result = authController.login(request);


        //Then
        assertEquals(expect, result);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Error.INVALID_USERNAME.getMessage()));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}