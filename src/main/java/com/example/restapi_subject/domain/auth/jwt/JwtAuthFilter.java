package com.example.restapi_subject.domain.auth.jwt;

import com.example.restapi_subject.global.common.response.ApiResponse;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import static com.example.restapi_subject.global.util.JwtUtil.extractBearer;


public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<String> whiteList = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/signup",
            "/swagger-ui/",
            "/v3/api-docs/"
    );

    /** 공개 GET경로: 게시글 목록/게시글 상세/댓글-목록 **/
    private static final Pattern BOARD_DETAIL = Pattern.compile("^/api/v1/boards/\\d+$");
    private static final Pattern BOARD_COMMENTS = Pattern.compile("^/api/v1/boards/\\d+/comments$");

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicGet(path, method)) {
            setUserIfValidTokenPresent(request);
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
        request.setAttribute("userId", userId);
        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(String path) {
        for (String prefix : whiteList) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    private boolean isPublicGet(String path, String method) {
        if (!"GET".equalsIgnoreCase(method)) return false;
        if ("/api/v1/boards".equals(path)) return true;
        if (BOARD_DETAIL.matcher(path).matches()) return true;
        if (BOARD_COMMENTS.matcher(path).matches()) return true;
        return false;
    }

    private void setUserIfValidTokenPresent(HttpServletRequest req) {
        String token = extractBearer(req.getHeader("Authorization"));
        if (token != null && jwtUtil.isValid(token) && !jwtUtil.isExpired(token)) {
            req.setAttribute("userId", jwtUtil.getUserId(token));
        }
    }

    private void writeError(HttpServletResponse response, ExceptionType type) throws IOException {
        response.setStatus(type.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> body = ApiResponse.error(type.getErrorMessage());
        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}
