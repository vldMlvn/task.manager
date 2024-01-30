package com.dev13.taskmanager.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CustomResponse<T> {

    private Error error;
    private T dto;

    public static <T> CustomResponse<T> success(T dto) {
        return CustomResponse
                .<T>builder()
                .error(Error.OK)
                .dto(dto)
                .build();
    }

    public static <T> CustomResponse<T> noBodySuccess(){
        return CustomResponse.
                <T>builder()
                .error(Error.OK)
                .dto(null)
                .build();
    }

    public static <T> CustomResponse<T> failed (Error error){
        return CustomResponse
                .<T>builder()
                .error(error)
                .dto(null)
                .build();
    }
}
