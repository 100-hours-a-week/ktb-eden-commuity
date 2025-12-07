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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    public UserRes.SimpleProfileDto getProfileByIdOrDeleted(Long userId) {
        return userRepository.findById(userId)
                .map(UserRes.SimpleProfileDto::from)
                .orElse(new UserRes.SimpleProfileDto("탈퇴한 사용자", "/images/default.png"));
    }

    public Map<Long, UserRes.SimpleProfileDto> getProfilesByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();
        return userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserRes.SimpleProfileDto::from
                ));
    }

    public UserRes.UserDto me(Long userId) {
        User u = getUserOrThrow(userId);
        return UserRes.UserDto.from(u);
    }

    @Transactional
    public UserRes.UpdateProfileDto updateProfile(Long userId, UserReq.UpdateProfileDto dto) {
        User user = getUserOrThrow(userId);
        validateUpdateNickname(user, dto);

        user.changeProfile(dto.nickname(), dto.profileImage());
        userRepository.save(user);
        return UserRes.UpdateProfileDto.from(user);
    }

    @Transactional
    public void changePassword(Long userId, UserReq.ChangePasswordDto dto) {
        User user = getUserOrThrow(userId);
        validateNewPassword(user, dto);
        String hashed = passwordUtil.hash(dto.newPassword());
        user.changePasswordHashed(hashed);
        authService.deleteRefreshToken(userId);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = getUserOrThrow(userId);

        authService.deleteRefreshToken(userId);
        user.softDelete();
        userRepository.save(user);
        eventPublisher.publishEvent(new UserEvent(userId, UserEvent.Type.DELETED));
    }

    /**
     * 내부 메소드
     */

    private void validateNewPassword(User user, UserReq.ChangePasswordDto dto) {
        if (!dto.newPassword().equals(dto.newPasswordConfirm())) {
            throw new CustomException(ExceptionType.PASSWORD_MISMATCH);
        }
        if (passwordUtil.matches(dto.newPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_SAME_AS_OLD);
        }
    }

    private void validateUpdateNickname(User before, UserReq.UpdateProfileDto dto) {
        if (dto.nickname() != null && !dto.nickname().equals(before.getNickname())
                && userRepository.existsByNickname(dto.nickname())) {
            throw new CustomException(ExceptionType.DUPLICATE_NICKNAME);
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));
    }
}
