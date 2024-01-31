package com.dev13.taskmanager.entity.dto;

import lombok.*;

import java.time.LocalDateTime;

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
}
