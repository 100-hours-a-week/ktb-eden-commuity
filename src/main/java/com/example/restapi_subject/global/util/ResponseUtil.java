package com.example.restapi_subject.global.util;

import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {
    private ResponseUtil() {throw new AssertionError("Utility class");}

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.ok(message, null));
    }
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.ok(message, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(message, data));
    }

    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }
    public static ResponseEntity<ApiResponse<Void>> error(ExceptionType type) {
        return error(type.getHttpStatus(), type.getErrorMessage());
    }
}
