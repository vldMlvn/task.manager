package com.dev13.taskmanager.entity.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    private UserDto user;
    private String token;
}
