package com.example.restapi_subject.domain.auth.jwt;

import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    JwtUtil jwtUtil;

    @Mock
    UserRepository userRepository;

    JwtAuthFilter jwtAuthFilter;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain filterChain;

    @BeforeEach
    void setup() {
        jwtAuthFilter = new JwtAuthFilter(jwtUtil, userRepository, List.of("/api/v1/auth/**"));

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("OPTIONS 요청은 필터 인증 없이 통과")
    void options_request_pass() throws Exception {
        request.setMethod("OPTIONS");
        request.setRequestURI("/api/v1/some-url");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("AT 없으면 TOKEN_MISSING 반환하고 chain.doFilter 호출되지 않음")
    void no_token_return_error() throws Exception {
        //given
        request.setRequestURI("/api/v1/boards/123");
        //when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        //then
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("token_missing");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("허용된 URL 요청은 토큰 없어도 chain.doFitler 호출")
    void allow_url_pass_through() throws Exception {
        //given
        request.setRequestURI("/api/v1/auth/login");
        //when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        //then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("토큰 invalid - TOKEN_INVALID")
    void invalid_token() throws Exception {
        //given
        request.setRequestURI("/api/v1/boards/1");
        request.addHeader("Authorization", "Bearer abc");

        when(jwtUtil.isValid("abc")).thenReturn(false);
        jwtAuthFilter.doFilter(request, response, filterChain);
        //when, then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("token_invalid");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("토큰 expired - EXPIRED_TOKEN")
    void expired_token() throws Exception {
        //given
        request.addHeader("Authorization", "Bearer expired");
        when(jwtUtil.isValid("expired")).thenReturn(true);
        when(jwtUtil.isExpired("expired")).thenReturn(true);
        jwtAuthFilter.doFilter(request, response, filterChain);
        //when, then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("token_expired");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("토큰 유효하지만 유저가 없으면 USER_NOT_FOUND")
    void user_not_found() throws Exception {
        //given
        request.setRequestURI("/api/v1/boards/1");
        request.addHeader("Authorization", "Bearer valid");

        when(jwtUtil.isValid("valid")).thenReturn(true);
        when(jwtUtil.isExpired("valid")).thenReturn(false);
        when(jwtUtil.getUserId("valid")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        
        //then
        assertThat(response.getContentAsString()).contains("user_not_found");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("삭제된 유저면 USER_ALREADY_DELETED 반환")
    void methodName() throws Exception {
        //given
        request.setRequestURI("/api/v1/boards");
        request.setMethod("POST");
        request.addHeader("Authorization", "Bearer valid");

        //when
        when(jwtUtil.isValid("valid")).thenReturn(true);
        when(jwtUtil.isExpired("valid")).thenReturn(false);
        when(jwtUtil.getUserId("valid")).thenReturn(1L);

        User deletedUser = User.builder()
                .id(1L)
                .email("deleted@test.com")
                .password("HASHED")
                .nickname("eden-deleted")
                .profileImage("img")
                .deleted(true)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(deletedUser));
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        //then
        assertThat(response.getStatus()).isEqualTo(ExceptionType.USER_ALREADY_DELETED.getHttpStatus().value());
        assertThat(response.getContentAsString()).contains("user_already_deleted");
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("정상 토큰이면 SecurityContext에 인증 저장 후 체인 통과")
    void valid_token_authenticates_and_passes() throws Exception {
        //given
        request.setRequestURI("/api/v1/boards/1");
        request.addHeader("Authorization", "Bearer valid");

        when(jwtUtil.isValid("valid")).thenReturn(true);
        when(jwtUtil.isExpired("valid")).thenReturn(false);
        when(jwtUtil.getUserId("valid")).thenReturn(1L);
        User user = User.builder()
                .id(1L)
                .email("ok@test.com")
                .password("HASHED")
                .nickname("eden")
                .profileImage("img")
                .deleted(false)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        //then
        verify(filterChain, times(1)).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(user);

        assertThat(request.getAttribute("userId")).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("비회원 모드 필터 스킵 - GET /api/v1/boards ~")
    void get_boards_public_access() throws Exception {
        //given
        request.setMethod("GET");
        request.setRequestURI("/api/v1/boards");
        //when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        //then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(200);
    }
}