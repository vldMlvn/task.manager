package com.dev13.taskmanager.entity.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;
    private String username;
    private String description;
    private LocalDateTime date;
    private LocalDateTime createDate;
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDto taskDto = (TaskDto) o;
        return isActive == taskDto.isActive && Objects.equals(id, taskDto.id) && Objects.equals(username, taskDto.username) && Objects.equals(description, taskDto.description) && Objects.equals(date, taskDto.date) && Objects.equals(createDate, taskDto.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, description, date, createDate, isActive);
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", createDate=" + createDate +
                ", isActive=" + isActive +
                '}';
    }
}

