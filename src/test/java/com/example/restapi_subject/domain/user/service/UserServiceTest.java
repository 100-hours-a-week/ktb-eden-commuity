package com.example.restapi_subject.domain.user.service;

import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.dto.UserReq;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.event.UserEvent;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.PasswordUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordUtil passwordUtil;

    @Mock
    AuthService authService;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("getProfileByIdOrDeleted - 존재하면 SimpleProfileDto 반환")
    void getProfileById_success() throws Exception {
        //given
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserRes.SimpleProfileDto res =  userService.getProfileByIdOrDeleted(user.getId());
        //when, then
        assertThat(res.nickname()).isEqualTo(user.getNickname());
        assertThat(res.profileImage()).isEqualTo(user.getProfileImage());
    }

    @Test
    @DisplayName("getProfileByIdOrDeleted - 없으면 탈퇴한 사용자 반환")
    void getProfileById_deleted() throws Exception {
        //given
        when(userRepository.findById((1L))).thenReturn(Optional.empty());

        UserRes.SimpleProfileDto res =  userService.getProfileByIdOrDeleted(1L);
        //when, then
        assertThat(res.nickname()).isEqualTo("탈퇴한 사용자");
    }

    @Test
    @DisplayName("getProfilesByIds - SimpleProfileDto 맵 반환")
    void getProfilesById_success() throws Exception {
        //given
        Set<Long> ids = Set.of(1L, 2L);
        User user1 = createUser();
        User user2 = User.builder()
                .id(2L)
                .email("oka@test.com")
                .password("HASHED")
                .nickname("eden2")
                .profileImage("img")
                .deleted(false)
                .build();

        when(userRepository.findAllByIdIn(ids)).thenReturn(List.of(user1, user2));
        //when
        Map<Long, UserRes.SimpleProfileDto> result = userService.getProfilesByIds(ids);
        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L).nickname()).isEqualTo("eden");
        assertThat(result.get(1L).profileImage()).isEqualTo("img");

        assertThat(result.get(2L).nickname()).isEqualTo("eden2");
        assertThat(result.get(2L).profileImage()).isEqualTo("img");

        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    @DisplayName("getProfilesByIds 실패 - null 이면 빈 맵")
    void getProfilesByIds_null() throws Exception {
        //given
        Map<Long, UserRes.SimpleProfileDto> result = userService.getProfilesByIds(null);
        //when, then
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getProfilesByIds 실패 - 빈 set 이면 빈 맵")
    void getProfilesByIds_empty() throws Exception {
        //given
        Map<Long, UserRes.SimpleProfileDto> result = userService.getProfilesByIds(Set.of());
        //when, then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("me 성공 - UserDto 반환")
    void me_success() throws Exception {
        //given
        User mock = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock));

        UserRes.UserDto res = userService.me(1L);

        //when, then
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.nickname()).isEqualTo(mock.getNickname());
    }

    @Test
    @DisplayName("me 실패 - USER_NOT_FOUND")
    void me_fail_user_not_found() throws Exception {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> userService.me(1L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.USER_NOT_FOUND.getErrorMessage());
    }

    @Test
    @DisplayName("updateProfile 성공 - UpadateProfileDto 반환")
    void updateProfile_success() throws Exception {
        //given
        User user = createUser();
        UserReq.UpdateProfileDto dto = new UserReq.UpdateProfileDto("newNick", "newImg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserRes.UpdateProfileDto res = userService.updateProfile(1L, dto);
        //then
        assertThat(res.nickname()).isEqualTo("newNick");
        assertThat(res.profileImage()).isEqualTo("newImg");
    }

    @Test
    @DisplayName("updateProfile 실패 - DUPLICATE_NICKNAME")
    void updateProfile_fail_duplicate_nickname() throws Exception {
        //given
        User user = createUser();
        UserReq.UpdateProfileDto dto = new UserReq.UpdateProfileDto("newNick", "newImg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(true);
        //when, then
        assertThatThrownBy(() -> userService.updateProfile(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.DUPLICATE_NICKNAME.getErrorMessage());
    }

    @Test
    @DisplayName("changePassword 성공 - void")
    void changePassword_success() throws Exception {
        //given
        User user = createUser();
        UserReq.ChangePasswordDto dto = new UserReq.ChangePasswordDto("newPass!", "newPass!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordUtil.matches("newPass!", user.getPassword())).thenReturn(false);
        when(passwordUtil.hash("newPass!")).thenReturn("HASHED_NEW");
        doNothing().when(authService).deleteRefreshToken(1L);

        //when
        userService.changePassword(1L, dto);

        //then
        verify(authService, times(1)).deleteRefreshToken(1L);
        verify(passwordUtil, times(1)).hash("newPass!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword 실패 - PASSWORD_MISMATCH")
    void changePassword_fail_password_mismatch() throws Exception {
        //given
        User user = createUser();
        UserReq.ChangePasswordDto dto = new UserReq.ChangePasswordDto("newPass!", "dif_newPass!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //when, then
        assertThatThrownBy(() -> userService.changePassword(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.PASSWORD_MISMATCH.getErrorMessage());
    }

    @Test
    @DisplayName("changePassword 실패 - PASSWORD_SAME_AS_OLD")
    void changePassword_fail_password_same_as_old() throws Exception {
        //given
        User user = createUser();
        UserReq.ChangePasswordDto dto = new UserReq.ChangePasswordDto("newPass!", "newPass!");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordUtil.matches("newPass!", user.getPassword())).thenReturn(true);

        //when,then
        assertThatThrownBy(() -> userService.changePassword(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.PASSWORD_SAME_AS_OLD.getErrorMessage());
    }

    @Test
    @DisplayName("deleteAccount 성공 - Void")
    void deleteAccount_success() throws Exception {
        //given
        User user = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(authService).deleteRefreshToken(1L);
        //when
        userService.deleteAccount(1L);
        //then
        verify(authService, times(1)).deleteRefreshToken(1L);
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(UserEvent.class));
    }

    private static User createUser() {
        return User.builder()
                .id(1L)
                .email("ok@test.com")
                .password("HASHED")
                .nickname("eden")
                .profileImage("img")
                .deleted(false)
                .build();
    }
}