package com.example.restapi_subject.domain.auth.jwt;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.auth.dto.AuthRes;
import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;

public class LoginFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final AuthService authService;

    private static final String LOGIN_PATH = "/api/v1/auth/login";

    public LoginFilter(AuthService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod()) && LOGIN_PATH.equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            AuthReq.LoginDto dto = objectMapper.readValue(request.getInputStream(), AuthReq.LoginDto.class);

            AuthRes.LoginDto loginRes = authService.login(dto);

            response.setHeader("Authorization", "Bearer " + loginRes.tokenDto().accessToken());
            response.addHeader("Access-Control-Expose-Headers", "Authorization");

            ResponseCookie rtCookie = ResponseCookie.from("refreshToken", loginRes.tokenDto().refreshToken())
                    .httpOnly(true)
                    .secure(false) // 로컬개발 false
                    .sameSite("Lax")
                    .path("/api/v1/auth")
                    .maxAge(Duration.ofDays(14))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, rtCookie.toString());

            ApiResponse<AuthRes.LoginDto> body = ApiResponse.ok("login_success", loginRes);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(body));
            response.getWriter().flush();
            // Filter단에서는 AdviceHandler가 예외처리 불가
        } catch (CustomException e) {
            ExceptionType type = e.getExceptionType();
            response.setStatus(type.getHttpStatus().value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(type.getErrorMessage())));
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(500);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error("internal_server_error")));
            response.getWriter().flush();
        }
    }
}

