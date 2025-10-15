package com.example.restapi_subject.domain.user.controller;

import com.example.restapi_subject.domain.user.dto.UserReq;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.service.UserService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<UserRes.UserDto> me(@RequestAttribute("userId") Long userId) {
        return ApiResponse.ok("user_me_success", userService.me(userId));
    }

    @PatchMapping
    public ApiResponse<UserRes.UpdateProfileDto> updateProfile(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserReq.UpdateProfileDto dto) {
        return ApiResponse.ok("user_profile_update_success", userService.updateProfile(userId, dto));
    }

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserReq.ChangePasswordDto dto) {
        userService.changePassword(userId, dto);
        return ApiResponse.ok("user_password_change_success", null);
    }

    @DeleteMapping
    public ApiResponse<Void> delete(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UserReq.DeleteAccountDto dto,
            HttpServletResponse response
    ) {
        userService.deleteAccount(userId, dto);
        ResponseCookie cleared = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 로컬 개발 시 false
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cleared.toString());
        return ApiResponse.ok("user_deleted_success", null);
    }
}
