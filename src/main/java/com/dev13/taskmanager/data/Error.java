package com.dev13.taskmanager.data;

import lombok.Getter;

@Getter
public enum Error {
    OK("OK"),
    INVALID_USERNAME("Invalid username"),
    INVALID_PASSWORD("Invalid password"),
    INVALID_EMAIL("Invalid email"),
    USERNAME_ALREADY_EXIST("Username already exist"),
    INVALID_DATE_RANGE("Invalid date range"),
    TASK_NOT_FOUND("Task not found"),
    TASKS_NOT_FOUND("Tasks not found"),
    ACTIVE_TASKS_NOT_FOUND("Active task not found"),
    USER_NOT_FOUND("User not found"),
    UNKNOWN_ERROR("Oops, something went wrong :(");

    private final String message;

    Error(String message) {
        this.message = message;
    }

}
