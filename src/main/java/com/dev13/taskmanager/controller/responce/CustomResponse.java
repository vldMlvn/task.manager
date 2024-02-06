package com.dev13.taskmanager.controller.responce;

import com.dev13.taskmanager.data.Error;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
public class CustomResponse<T> {

    private LocalDateTime timestamp;
    private String message;
    private T body;

    public static <T> CustomResponse<T> success(T dto) {
        return CustomResponse
                .<T>builder()
                .timestamp(LocalDateTime.now())
                .message(Error.OK.getMessage())
                .body(dto)
                .build();
    }

    public static <T> CustomResponse<T> noBodySuccess() {
        return CustomResponse.
                <T>builder()
                .timestamp(LocalDateTime.now())
                .message(Error.OK.getMessage())
                .body(null)
                .build();
    }

    public static <T> CustomResponse<T> failed(Error error) {
        return CustomResponse
                .<T>builder()
                .timestamp(LocalDateTime.now())
                .message(error.getMessage())
                .body(null)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomResponse<?> that = (CustomResponse<?>) o;
        return Objects.equals(message, that.message) && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, body);
    }

    @Override
    public String toString() {
        return "CustomResponse{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", body=" + body +
                '}';
    }
}
