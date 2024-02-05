package com.dev13.taskmanager.entity.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    private UserDto user;
    private String token;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthDto authDto = (AuthDto) o;
        return Objects.equals(user, authDto.user) && Objects.equals(token, authDto.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, token);
    }

    @Override
    public String toString() {
        return "AuthDto{" +
                "user=" + user +
                ", token='" + token + '\'' +
                '}';
    }
}
