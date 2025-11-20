package com.example.restapi_subject.domain.auth.service;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTestMocking {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공-생성된 사용자 ID를 반환")
    void signUp_success() {
        //given
        AuthReq.SignUpDto dto = new AuthReq.SignUpDto(
                "ok@test.com",
                "Test1234!",
                "Test1234!",
                "eden",
                "https://img"
        );
        Mockito.when(passwordUtil.hash("Test1234!")).thenReturn("HASHED_PW");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.withId(1L);
        });

        // when
        Long savedUserId = authService.signUp(dto);

        // then
        assertThat(savedUserId).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원가입 실패 - DUPLICATE_EMAIL")
    void signUp_duplicateEmail() {
        // given
        AuthReq.SignUpDto dto1 = new AuthReq.SignUpDto(
                "dup@test.com", "Test1234!", "Test1234!", "eden2", "https://img2"
        );
        AuthReq.SignUpDto dto2 = new AuthReq.SignUpDto(
                "dup@test.com", "Test1234!", "Test1234!", "eden2", "https://img2"
        );
        Mockito.when(userRepository.existsByEmail(dto1.email()))
                .thenReturn(false)
                .thenReturn(true);
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.withId(u.getId());
        });

        //when
        authService.signUp(dto1);

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> authService.signUp(dto2))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.DUPLICATE_EMAIL.getErrorMessage());
        Mockito.verify(userRepository, times(2)).existsByEmail(dto1.email());
        Mockito.verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - PASSWORD_MISMATCH")
    void signUp_passwordMismatch() {
        // given
        AuthReq.SignUpDto dto = new AuthReq.SignUpDto(
                "pw@test.com", "Test1234!", "Different!", "eden", "https://img"
        );
        // when
        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> authService.signUp(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.PASSWORD_MISMATCH.getErrorMessage());

    }

    @Test
    @DisplayName("회원가입 실패 - DUPLICATE_NICKNAME")
    void signUp_duplicateNickName() throws Exception {
        //given
        AuthReq.SignUpDto dto1 = new AuthReq.SignUpDto(
                "dtoOne@test.com", "Test1234!", "Test1234!", "eden", "https://img"
        );
        AuthReq.SignUpDto dto2 = new AuthReq.SignUpDto(
                "dtoTwo@test.com", "Test1234!", "Test1234!", "eden", "https://img"
        );
        Mockito.when(userRepository.existsByNickname(dto1.nickname()))
                .thenReturn(false)
                .thenReturn(true);
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.withId(u.getId());
        });

        //when
        authService.signUp(dto1);

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> authService.signUp(dto2))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.DUPLICATE_NICKNAME.getErrorMessage());
        Mockito.verify(userRepository, times(2)).existsByNickname(dto1.nickname());
        Mockito.verify(userRepository, times(1)).save(any(User.class));
    }
}