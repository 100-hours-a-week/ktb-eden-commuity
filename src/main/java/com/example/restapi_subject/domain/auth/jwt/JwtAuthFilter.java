package com.example.restapi_subject.domain.auth.jwt;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

import static com.example.restapi_subject.global.util.JwtUtil.extractBearer;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final List<String> allowUrls;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository, List<String> allowedUrls) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.allowUrls = allowedUrls;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();

        if (shouldSkip(path, request.getMethod(), request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        String token = extractBearer(authorization);

        if (token == null) {
            writeError(response, ExceptionType.TOKEN_MISSING);
            return;
        }
        if (!jwtUtil.isValid(token)) {
            writeError(response, ExceptionType.TOKEN_INVALID);
            return;
        }
        if (jwtUtil.isExpired(token)) {
            writeError(response, ExceptionType.TOKEN_EXPIRED);
            return;
        }

        Long userId = jwtUtil.getUserId(token);

        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            writeError(response, ExceptionType.USER_NOT_FOUND);
            return;
        }
        if (user.isDeleted()) {
            writeError(response, ExceptionType.USER_ALREADY_DELETED);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setAttribute("userId", userId);
        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(String path, String method, HttpServletRequest request) {
        for (String prefix : allowUrls) {
            if (path.startsWith(prefix.replace("/**", ""))) return true;
        }

        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/v1/boards")) {
            String authorization = request.getHeader("Authorization");
            return (authorization == null || authorization.isBlank());
        }
        return false;
    }


    private void writeError(HttpServletResponse response, ExceptionType type) throws IOException {
        response.setStatus(type.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> body = ApiResponse.error(type.getErrorCode(), type.getErrorMessage());
        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}
