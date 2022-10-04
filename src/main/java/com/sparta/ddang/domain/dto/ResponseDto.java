package com.sparta.ddang.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseDto<T> {
    private int statusCode;
    private String msg;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(200, "OK", data);
    }

    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(500, message, null);
    }

    public static ResponseDto<?> successToMessage(int i, String some, Object t) {
        return new ResponseDto<>(200, "OK", t);
    }

    @Builder
    public ResponseDto(int statusCode, String msg, T data) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.data = data;
    }
    
}