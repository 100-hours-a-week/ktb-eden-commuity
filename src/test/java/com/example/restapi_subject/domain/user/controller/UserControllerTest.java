package com.example.restapi_subject.domain.user.controller;

import com.example.restapi_subject.domain.user.dto.UserReq;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.service.UserService;
import com.example.restapi_subject.global.util.TokenResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    TokenResponseWriter tokenResponseWriter;

    @Autowired
    ObjectMapper objectMapper;
    
    @Test
    @DisplayName("내 정보 조회 성공")
    void me_success() throws Exception {
        //given
        Long userId = 1L;
        UserRes.UserDto mockDto = new UserRes.UserDto(1L, "ok@test.com", "eden", "img");

        when(userService.me(userId)).thenReturn(mockDto);
        
        //when, then
        mockMvc.perform(
                get("/api/v1/users")
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_me_success"))
                .andExpect(jsonPath("$.data.nickname").value("eden"));
    }
    
    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_success() throws Exception {
        //given
        Long userId = 1L;
        UserReq.UpdateProfileDto req = new UserReq.UpdateProfileDto("newNick", "newImg");
        UserRes.UpdateProfileDto res = new UserRes.UpdateProfileDto(1L, "newNick", "newImg");

        Mockito.when(userService.updateProfile(eq(userId), any())).thenReturn(res);

        //when, then
        mockMvc.perform(
                patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_profile_update_success"))
                .andExpect(jsonPath("$.data.nickname").value("newNick"));
    }

    @Test
    @DisplayName("프로필 업데이트 실패 - nickname_required(null)")
    void updateProfile_fail_nickname_required() throws Exception {
        //given
        Long userId = 1L;
        UserReq.UpdateProfileDto req = new UserReq.UpdateProfileDto(null, "newImg");
        //when, then
        mockMvc.perform(
                patch("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
                    .requestAttr("userId", userId)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU014"));
    }

    @Test
    @DisplayName("프로필 업데이트 실패 - nickname_max_10")
    void updateProfile_fail_nickname_max_10() throws Exception {
        //given
        Long userId = 1L;
        UserReq.UpdateProfileDto req = new UserReq.UpdateProfileDto("too_long_nickname", "newImg");
        //when, then
        mockMvc.perform(
                patch("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
                    .requestAttr("userId", userId)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU018"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() throws Exception {
        //given
        Long userId = 1L;
        UserReq.ChangePasswordDto req = new UserReq.ChangePasswordDto("newPassword123!", "newNewPassword123!");
        Mockito.doNothing().when(userService).changePassword(eq(userId), any());

        //when, then
        mockMvc.perform(
                patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_password_change_success"));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - new_password_required")
    void changePassword_fail_new_password_required() throws Exception {
        //given
        Long userId = 1L;
        UserReq.ChangePasswordDto req = new UserReq.ChangePasswordDto(null, "Test1234!");

        //when, then
        mockMvc.perform(
                patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU012"));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - password_rule_violation(pattern)")
    void changePassword_fail_password_rule_violation() throws Exception {
        //given
        Long userId = 1L;
        UserReq.ChangePasswordDto req = new UserReq.ChangePasswordDto("INVALID", "INVALID");

        //when, then
        mockMvc.perform(
                patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AU016"));
    }
    
    @Test
    @DisplayName("유저 삭제 성공 - RT 쿠키 삭제 포함")
    void delete_success() throws Exception {
        //given
        Long userId = 1L;

        Mockito.doNothing().when(userService).deleteAccount(userId);
        Mockito.doNothing().when(tokenResponseWriter).clearRefreshToken(any());

        //when, then
        mockMvc.perform(
                delete("/api/v1/users")
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_deleted_success"));
        Mockito.verify(userService).deleteAccount(userId);
        Mockito.verify(tokenResponseWriter).clearRefreshToken(any());
    }

}