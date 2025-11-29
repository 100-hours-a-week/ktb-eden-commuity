package com.example.restapi_subject.domain.auth.controller;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.auth.dto.AuthRes;
import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.JwtUtil;
import com.example.restapi_subject.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "AuthReq.SignUpDto 기반으로 회원을 저장합니다.")
    public ResponseEntity<ApiResponse<AuthRes.SignUpDto>> signup(@RequestBody @Valid AuthReq.SignUpDto signUpDto) {
        Long userId = authService.signUp(signUpDto);
        return ResponseUtil.created("register_success", new AuthRes.SignUpDto(userId));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "AuthReq.LoginDto 기반으로 로그인합니다.")
    public ApiResponse<AuthRes.LoginDto> login(
            @RequestBody AuthReq.LoginDto loginDto,
            HttpServletResponse response){
        AuthRes.LoginDto loginRes = authService.login(loginDto);

        if (loginRes == null || loginRes.tokenDto() == null) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
        }
        response.setHeader("Authorization", "Bearer " + loginRes.tokenDto().accessToken());
        response.addHeader("Access-Control-Expose-Headers", "Authorization");

        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", loginRes.tokenDto().refreshToken())
                .httpOnly(true)
                .secure(false) // 로컬개발 false
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, rtCookie.toString());

        return ApiResponse.ok("login_success", loginRes);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "토큰(RT)기반으로 로그아웃 합니다.")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String rt = authService.extractRefresh(request)
                .orElseThrow(() -> new CustomException(ExceptionType.TOKEN_MISSING));
        authService.logout(rt);

        ResponseCookie cleared = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)  // 로컬 개발 false
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(SET_COOKIE, cleared.toString());
        return ApiResponse.ok("logout_success", null);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "RTR기반 새 토큰을 받습니다.")
    public ApiResponse<AuthRes.TokenDto> refresh(
            @Parameter(hidden = true)
            @RequestHeader(value = AUTHORIZATION, required = false) String accessToken,
            HttpServletRequest request, HttpServletResponse response) {
        String extractedAt = JwtUtil.extractBearer(accessToken);
        String rt = authService.extractRefresh(request)
                .orElseThrow(() -> new CustomException(ExceptionType.TOKEN_MISSING));

        AuthRes.TokenDto tokens = authService.refresh(extractedAt, rt);

        response.setHeader("Authorization", "Bearer " + tokens.accessToken());
        response.addHeader("Access-Control-Expose-Headers", "Authorization");

        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(false)  // 로컬 개발 false
                .sameSite("Lax")
                .path("/")
                .maxAge(java.time.Duration.ofDays(14))
                .build();
        response.addHeader(SET_COOKIE, rtCookie.toString());

        return ApiResponse.ok("token_refreshed_success", tokens);
    }

}
