package com.dev13.taskmanager.controller;

import com.dev13.taskmanager.controller.request.LoginRequest;
import com.dev13.taskmanager.controller.request.RegisterRequest;
import com.dev13.taskmanager.controller.responce.CustomResponse;
import com.dev13.taskmanager.entity.dto.AuthDto;
import com.dev13.taskmanager.entity.dto.UserDto;
import com.dev13.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public CustomResponse<UserDto> register(@RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public CustomResponse<AuthDto> login(@RequestBody LoginRequest request) {
        return service.login(request);
    }
}
