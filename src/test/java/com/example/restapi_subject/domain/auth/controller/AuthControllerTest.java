package com.example.restapi_subject.domain.auth.controller;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.auth.dto.AuthRes;
import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.global.error.handler.GlobalExceptionHandler;
import com.example.restapi_subject.global.util.TokenResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenResponseWriter tokenResponseWriter;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 - register_success 반환")
    void signUp_success() throws Exception {
        // given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;
        Mockito.when(authService.signUp(any(AuthReq.SignUpDto.class)))
                .thenReturn(1L);

        // when
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("register_success"))
                .andExpect(jsonPath("$.data.user_id").value(1L));
    }

    @Test
    @DisplayName("로그인 성공 - login_success 반환")
    void login_success() throws Exception {
        // given
        AuthRes.TokenDto tokenDto = new AuthRes.TokenDto("newAT", "newRT");
        AuthRes.LoginDto loginDto = new AuthRes.LoginDto(1L, tokenDto);
        Mockito.when(authService.login(any(AuthReq.LoginDto.class)))
                .thenReturn(loginDto);

        String body = """
                {
                    "email": "ok@Test.com",
                    "password": "Test1234!"
                }
                """;

        // when
        mockMvc.perform(
                post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("login_success"))
                .andExpect(jsonPath("$.data.user_id").value(1L));

        Mockito.verify(tokenResponseWriter).writeAuthTokens(any(), eq(tokenDto));
    }

    @Test
    @DisplayName("로그아웃 성공 - logout_success 반환")
    void logout_success() throws Exception {
        // given
        String refreshToken = "mock-refresh-token";

        Mockito.when(authService.extractRefresh(any(HttpServletRequest.class)))
                .thenReturn(Optional.of(refreshToken));

        Mockito.doNothing().when(tokenResponseWriter)
                .clearRefreshToken(any(HttpServletResponse.class));

        // when
        mockMvc.perform(
                post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
        )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("logout_success"));

        // RT 추출
        Mockito.verify(authService, Mockito.times(1))
                .extractRefresh(any(HttpServletRequest.class));

        // logout 호출
        Mockito.verify(authService, Mockito.times(1))
                .logout(refreshToken);

        // 쿠키 제거
        Mockito.verify(tokenResponseWriter, Mockito.times(1))
                .clearRefreshToken(any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("토큰갱신 성공 - token_refreshed_success 반환")
    void refresh_success() throws Exception {
        //given
        String accessToken = "Bearer oldAccessToken";
        String extractedAt = "oldAccessToken";
        String refreshToken = "stored-refresh-token";

        AuthRes.TokenDto tokenDto = new AuthRes.TokenDto("new-access-token", "new-refresh-token");

        Mockito.when(authService.extractRefresh(any(HttpServletRequest.class)))
                .thenReturn(Optional.of(refreshToken));

        Mockito.when(authService.refresh(anyString(), eq(refreshToken)))
                .thenReturn(tokenDto);

        //when
        mockMvc.perform(
                post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
        )
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("token_refreshed_success"))
                .andExpect(jsonPath("$.data.access_token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("new-refresh-token"));

        Mockito.verify(authService, Mockito.times(1))
                .extractRefresh(any(HttpServletRequest.class));

        Mockito.verify(authService, Mockito.times(1))
                .refresh(extractedAt, refreshToken);

        Mockito.verify(tokenResponseWriter, Mockito.times(1))
                .writeAuthTokens(any(HttpServletResponse.class), eq(tokenDto));
    }

    @Test
    @DisplayName("회원가입 실패 - email_required")
    void signUp_fail_email_required() throws Exception {
        // given
        String body = """
            {
              "email": "",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        // when
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU011"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("회원가입 실패 - email_invalid")
    void signUp_fail_email_invalid() throws Exception {
        // given
        String body_abc = """
            {
              "email": "abc",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        // when
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body_abc)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU010"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("회원가입 실패 - password_required")
    void signUp_fail_password_required() throws Exception {
        // given
        String body = """
            {
              "email": "ok@test.com",
              "password": "",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        // when
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU012"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("회원가입 실패 - password_rule_violation")
    void signUp_fail_password_rule_violation() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "asd",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU016"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("회원가입 실패 - passwordConfirm_required")
    void signUp_fail_passwordConfirm_required() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "",
              "nickname": "eden",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU013"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("회원가입 실패 - nickname_required")
    void signUp_fail_nickname_required() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU014"));

        Mockito.verify(authService, never()).signUp(any());
    }
    @Test
    @DisplayName("회원가입 실패 - nickname_max_10")
    void signUp_fail_nickname_max_10() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "edenadsdkdkd",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU018"));

        Mockito.verify(authService, never()).signUp(any());
    }
    @Test
    @DisplayName("회원가입 실패 - nickname_no_space")
    void signUp_fail_nickname_no_space() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "ede n",
              "profile_image": "https://picsum.photos/seed/ok/200/200"
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU017"));

        Mockito.verify(authService, never()).signUp(any());
    }
    @Test
    @DisplayName("회원가입 실패 - profileImage_required")
    void signUp_fail_profileImage_required() throws Exception {
        //given
        String body = """
            {
              "email": "ok@test.com",
              "password": "Test1234!",
              "password_confirm": "Test1234!",
              "nickname": "eden",
              "profile_image": ""
            }
            """;

        //when + then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU015"));

        Mockito.verify(authService, never()).signUp(any());
    }

    @Test
    @DisplayName("로그인 실패 - email_required")
    void login_fail_email_required() throws Exception {
        String body = """
        {
          "email": "",
          "password": "Test1234!"
        }
        """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU011"));

        Mockito.verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("로그인 실패 - email_invalid")
    void login_fail_email_invalid() throws Exception {
        String body = """
        {
          "email": "abc",
          "password": "Test1234!"
        }
        """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU010"));
        Mockito.verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("로그인 실패 - password_required")
    void login_fail_password_required() throws Exception {
        String body = """
        {
          "email": "ok@test.com",
          "password": ""
        }
        """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU012"));

        Mockito.verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("로그인 실패 - password_rule_violation")
    void login_fail_password_rule_violation() throws Exception {
        String body = """
        {
          "email": "ok@test.com",
          "password": "abc"
        }
        """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU016"));

        Mockito.verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("토큰갱신 실패 - token_missing(no_refreshToken)")
    void refresh_fail_no_refreshToken() throws Exception {

        Mockito.when(authService.extractRefresh(any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer oldAT")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("T002"));

        Mockito.verify(authService, never()).refresh(any(), any());
    }


}
