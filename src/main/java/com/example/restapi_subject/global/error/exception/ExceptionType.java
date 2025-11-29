package com.example.restapi_subject.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {


    // AUTH, USER
    DUPLICATE_EMAIL("AU001","email_duplicate", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH("AU002","password_mismatch", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("AU003", "nickname_duplicate", HttpStatus.CONFLICT),
    ID_ALREADY_EXISTS("AU004", "id_already_exists", HttpStatus.CONFLICT),
    USER_ALREADY_CREATED("AU005", "user_already_created", HttpStatus.CONFLICT),
    USER_NOT_FOUND("AU006", "user_not_found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("AU007", "invalid_credentials", HttpStatus.UNAUTHORIZED),
    PASSWORD_SAME_AS_OLD("AU008", "password_same_as_old", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED("AU009", "user_already_deleted", HttpStatus.CONFLICT),
    INVALID_EMAIL_FORMAT("AU010", "email_invalid", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("AU011", "email_required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("AU012", "password_required", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_REQUIRED("AU013", "passwordConfirm_required", HttpStatus.BAD_REQUEST),
    NICKNAME_REQUIRED("AU014", "nickname_required", HttpStatus.BAD_REQUEST),
    PROFILE_IMAGE_REQUIRED("AU015", "profileImage_required", HttpStatus.BAD_REQUEST),
    PASSWORD_RULE_VIOLATION("AU016", "password_rule_violation", HttpStatus.BAD_REQUEST),
    NICKNAME_RULE_VIOLATION("AU017", "nickname_no_space", HttpStatus.BAD_REQUEST),
    NICKNAME_MAX("AU018", "nickname_max_10", HttpStatus.BAD_REQUEST),

    // TOKEN
    TOKEN_NOT_FOUND("T001", "token_not_found", HttpStatus.NOT_FOUND),
    TOKEN_MISSING("T002", "token_missing",  HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("T003", "token_invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("T004", "token_expired", HttpStatus.UNAUTHORIZED) ,
    TOKEN_NOT_REFRESH("T005", "token_not_refresh", HttpStatus.BAD_REQUEST) ,
    TOKEN_STOLEN("T006", "token_stolen", HttpStatus.UNAUTHORIZED) ,
    ACCESS_DENIED("T007", "access_denied", HttpStatus.FORBIDDEN),

    // BOARD
    BOARD_ALREADY_CREATED("B001", "board_already_created", HttpStatus.CONFLICT),
    BOARD_NOT_FOUND("B002", "board_not_found", HttpStatus.NOT_FOUND),
    TITLE_REQUIRED("B003", "title_required", HttpStatus.BAD_REQUEST),
    CONTENT_REQUIRED("B004", "content_required", HttpStatus.BAD_REQUEST),
    ALREADY_LIKED("B005", "already_liked", HttpStatus.CONFLICT),
    NOT_LIKED("B006", "not_liked", HttpStatus.BAD_REQUEST),

    // COMMENT
    COMMENT_NOT_FOUND("C001", "comment_not_found", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_CREATED("C002", "comment_already_created", HttpStatus.CONFLICT),

    // ETC
    FILE_EMPTY("S001", "file_empty", HttpStatus.BAD_REQUEST),
    INVALID_JSON("S002", "invalid_json", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("S999", "internal_server_error",HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}
