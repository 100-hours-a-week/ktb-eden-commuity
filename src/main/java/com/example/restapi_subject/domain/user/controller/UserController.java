package com.example.restapi_subject.domain.user.controller;

import com.example.restapi_subject.domain.user.dto.UserReq;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.service.UserService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.util.TokenResponseWriter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final TokenResponseWriter tokenResponseWriter;

    @GetMapping
    @Operation(summary = "내정보 조회", description = "토큰(AT)기반으로 내정보를 조회합니다.")
    public ApiResponse<UserRes.UserDto> me(@RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("user_me_success", userService.me(userId));
    }

    @PatchMapping
    @Operation(summary = "내정보 업데이트", description = "토큰(AT)검증후 내정보(닉네임,프로필사진)을 수정합니다.")
    public ApiResponse<UserRes.UpdateProfileDto> updateProfile(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserReq.UpdateProfileDto dto) {
        return ApiResponse.ok("user_profile_update_success", userService.updateProfile(userId, dto));
    }

    // TODO : 업데이트 시 RT 무효화?
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 업데이트", description = "토큰(AT)검증후 비밀번호를 수정합니다.")
    public ApiResponse<Void> changePassword(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserReq.ChangePasswordDto dto) {
        userService.changePassword(userId, dto);
        return ApiResponse.ok("user_password_change_success", null);
    }

    @DeleteMapping
    @Operation(summary = "유저 삭제", description = "토큰(AT)검증후 유저를 삭제합니다.")
    public ApiResponse<Void> delete(
            @RequestAttribute("userId") Long userId,
            HttpServletResponse response
    ) {
        userService.deleteAccount(userId);
        tokenResponseWriter.clearRefreshToken(response);
        return ApiResponse.ok("user_deleted_success", null);
    }
}
