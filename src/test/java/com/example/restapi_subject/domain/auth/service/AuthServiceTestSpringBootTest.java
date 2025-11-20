package com.example.restapi_subject.domain.auth.service;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AuthServiceTestSpringBootTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository.clear();
    }

    @Test
    @DisplayName("회원가입 성공-생성된 사용자 ID를 반환")
    void signUp_success() {
        // given
        AuthReq.SignUpDto dto = new AuthReq.SignUpDto(
                "ok@test.com",
                "Test1234!",
                "Test1234!",
                "eden",
                "https://img"
        );


        // when
        Long id = authService.signUp(dto);

        // then
        assertThat(id).isNotNull().isGreaterThan(0L);

        User saved = userRepository.findById(id).orElseThrow();
        assertThat(saved.getEmail()).isEqualTo("ok@test.com");
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

        //when
        authService.signUp(dto1);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> authService.signUp(dto2));

        //then
        assertThat(e.getMessage()).isEqualTo(ExceptionType.DUPLICATE_EMAIL.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - PASSWORD_MISMATCH")
    void signUp_passwordMismatch() {
        // given
        AuthReq.SignUpDto dto = new AuthReq.SignUpDto(
                "pw@test.com", "Test1234!", "Different!", "eden", "https://img"
        );

        // when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> authService.signUp(dto));

        // then
        assertThat(e.getMessage()).isEqualTo(ExceptionType.PASSWORD_MISMATCH.getErrorMessage());
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

        //when
        authService.signUp(dto1);
        CustomException e = Assertions.assertThrows(CustomException.class, () -> authService.signUp(dto2));

        //then
        assertThat(e.getMessage()).isEqualTo(ExceptionType.DUPLICATE_NICKNAME.getErrorMessage());
    }
}