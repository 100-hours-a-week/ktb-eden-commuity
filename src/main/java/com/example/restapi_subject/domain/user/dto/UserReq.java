package com.example.restapi_subject.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserReq {

    @Schema(name = "UserReqUpdateProfileDto")
    public static record UpdateProfileDto(
            @NotBlank(message = "nickname_required")
            @Size(max = 10, message = "nickname_max_10")
            String nickname,
            String profileImage
    ) {}

    @Schema(name = "UserReqChangePasswordDto")
    public static record ChangePasswordDto(
            @NotBlank(message = "new_password_required")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,20}$",
                    message = "password_rule_violation"
                    // 대문자,소문자,숫자,특수문자 각각 최소 1개씩, 8자 이상 20자 이하
            )
            String newPassword,
            @NotBlank(message = "new_password_confirm_required") String newPasswordConfirm
    ) {}
}
