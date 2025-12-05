package com.example.restapi_subject.domain.auth.controller;

import com.example.restapi_subject.domain.auth.repository.RefreshTokenRepository;
import com.example.restapi_subject.domain.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Refresh 실패 - TOKEN_MISSING(Authorization 헤더 없음)")
    void refresh_fail_no_authorization_real_flow() throws Exception {

        // given
        Long userId = 1L;
        String rt = "valid-token-123";
        refreshTokenRepository.save(userId, rt);
        Cookie cookie = new Cookie("refreshToken", rt);

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("token_missing"));
    }


}
