package com.example.restapi_subject.global.error.handler;

import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @RequestBody @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .orElse("validation_error");
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, message);
    }

    // JSON 파싱 실패 등
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, ExceptionType.INVALID_JSON.getErrorMessage());
    }

    // DB 무결성 제약 위반 (ex: UNIQUE, FK 등)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("⚠Data integrity violation: {}", ex.getMessage());

        // 예외 메시지 기반으로 세분화 가능
        if (ex.getMessage() != null && ex.getMessage().contains("board_like")) {
            // 중복 좋아요 예외 처리
            return ResponseUtil.error(ExceptionType.ALREADY_LIKED);
        }

        // 그 외 제약 조건 위반 (ex: FK 에러 등)
        return ResponseUtil.error(HttpStatus.CONFLICT, ExceptionType.SERVER_ERROR.getErrorMessage());
    }

    // 커스텀 비즈니스 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException ex) {
        return ResponseUtil.error(ex.getExceptionType());
    }

    // 그 외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
        log.error("Unexpected", ex);
        return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionType.SERVER_ERROR.getErrorMessage());
    }
}
