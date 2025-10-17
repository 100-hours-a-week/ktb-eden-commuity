package com.example.restapi_subject.domain.auth.dto;

import jakarta.validation.constraints.*;

public record AuthReq() {
    public record SignUpDto(
            @NotBlank(message = "email_required")
            @Pattern(
                    // 이메일은 영문과 @,.만 사용이 가능함
                    // ^$ 빈문자열은 패턴통과 -> email_required 띄우려고
                    regexp = "^$|^[A-Za-z]+@[A-Za-z]+\\.[A-Za-z]{2,10}$",
                    message = "email_invalid"
            )
            String email,

            @NotBlank(message = "password_required")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,20}$",
                    message = "password_rule_violation"
                    // 대문자,소문자,숫자,특수문자 각각 최소 1개씩, 8자 이상 20자 이하
            )
            String password,

            @NotBlank(message = "passwordConfirm_required")
            String passwordConfirm,

            @NotBlank(message = "nickname_required")
            @Size(max = 10, message = "nickname_max_10")
            @Pattern(regexp = "^[^\\s]+$", message = "nickname_no_space")
            String nickname,

            @NotBlank(message = "profileImage_required")
            String profileImage
    ) {
    }

    public record LoginDto(
            @NotBlank(message = "email_required")
            @Pattern(
                    // 이메일은 영문과 @,.만 사용이 가능함
                    // ^$ 빈문자열은 패턴통과 -> email_required 띄우려고
                    regexp = "^$|^[A-Za-z]+@[A-Za-z]+\\.[A-Za-z]{2,10}$",
                    message = "email_invalid"
            )
            String email,
            @NotBlank(message = "password_required")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,20}$",
                    message = "password_rule_violation"
                    // 대문자,소문자,숫자,특수문자 각각 최소 1개씩, 8자 이상 20자 이하
            )
            String password
    ){
    }
}
