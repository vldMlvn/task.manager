package com.dev13.taskmanager.controller.responce;

import com.dev13.taskmanager.data.Error;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}
