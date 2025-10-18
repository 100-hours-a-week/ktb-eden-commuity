package com.example.restapi_subject.global.error.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ExceptionType exceptionType;

    public CustomException(ExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());
        this.exceptionType = exceptionType;
    }
}
