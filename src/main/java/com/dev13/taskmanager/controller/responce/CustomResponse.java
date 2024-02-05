package com.dev13.taskmanager.controller.responce;

import com.dev13.taskmanager.data.Error;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Builder
@Getter
@Setter
public class CustomResponse<T> {

    private Error error;
    private T body;

    public static <T> CustomResponse<T> success(T dto) {
        return CustomResponse
                .<T>builder()
                .error(Error.OK)
                .body(dto)
                .build();
    }

    public static <T> CustomResponse<T> noBodySuccess() {
        return CustomResponse.
                <T>builder()
                .error(Error.OK)
                .body(null)
                .build();
    }

    public static <T> CustomResponse<T> failed(Error error) {
        return CustomResponse
                .<T>builder()
                .error(error)
                .body(null)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomResponse<?> that = (CustomResponse<?>) o;
        return error == that.error && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, body);
    }

    @Override
    public String toString() {
        return "CustomResponse{" +
                "error=" + error +
                ", body=" + body +
                '}';
    }
}
