package com.example.restapi_subject.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserReq() {

    public record UpdateProfileDto(
            @NotBlank(message = "nickname_required")
            @Size(max = 10, message = "nickname_max_10")
            String nickname,
            String profileImage
    ) {}

    public record ChangePasswordDto(
            @NotBlank(message = "new_password_required") String newPassword,
            @NotBlank(message = "new_password_confirm_required") String newPasswordConfirm
    ) {}

    public record DeleteAccountDto(
            @NotBlank(message = "password_required")
            String password
    ) {}



}
