package com.dev13.taskmanager.controller.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    private String description;
    private LocalDateTime date;
}
