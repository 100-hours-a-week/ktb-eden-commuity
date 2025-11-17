package com.example.restapi_subject.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {


    // AUTH, USER
    DUPLICATE_EMAIL("email_duplicate", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH("password_mismatch", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("nickname_duplicate", HttpStatus.CONFLICT),
    ID_ALREADY_EXISTS("id_already_exists", HttpStatus.CONFLICT),
    USER_ALREADY_CREATED("user_already_created", HttpStatus.CONFLICT),
    USER_NOT_FOUND("user_not_found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("invalid_credentials", HttpStatus.UNAUTHORIZED),
    PASSWORD_SAME_AS_OLD("password_same_as_old", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED("user_already_deleted", HttpStatus.CONFLICT),

    // TOKEN
    TOKEN_NOT_FOUND("token_not_found", HttpStatus.NOT_FOUND),
    TOKEN_MISSING("token_missing",  HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("token_invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("token_expired", HttpStatus.UNAUTHORIZED) ,
    TOKEN_NOT_REFRESH("token_not_refresh", HttpStatus.BAD_REQUEST) ,
    ACCESS_DENIED("access_denied", HttpStatus.FORBIDDEN),

    // BOARD
    BOARD_ALREADY_CREATED("board_already_created", HttpStatus.CONFLICT),
    BOARD_NOT_FOUND("board_not_found", HttpStatus.NOT_FOUND),
    TITLE_REQUIRED("title_required", HttpStatus.BAD_REQUEST),
    CONTENT_REQUIRED("content_required", HttpStatus.BAD_REQUEST),

    // COMMENT
    COMMENT_NOT_FOUND("comment_not_found", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_CREATED("comment_already_created", HttpStatus.CONFLICT),
    ALREADY_LIKED("already_liked", HttpStatus.CONFLICT),
    NOT_LIKED("not_liked", HttpStatus.BAD_REQUEST),

    // ETC
    FILE_EMPTY("file_empty", HttpStatus.BAD_REQUEST),
    INVALID_JSON("invalid_json", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("internal_server_error",null);

    private final String errorMessage;
    private final HttpStatus httpStatus;
}
