package com.example.restapi_subject.domain.user.service;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.dto.UserReq;
import com.example.restapi_subject.domain.user.dto.UserRes;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final AuthService authService;

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
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, UserReq.DeleteAccountDto dto) {
        User user = getUserOrThrow(userId);
        checkPasswordOrThrow(dto, user);

        AuthReq.DeleteRefreshTokenDto deleteRefreshTokenDto = new AuthReq.DeleteRefreshTokenDto(dto.password());
        authService.deleteRefreshToken(userId, deleteRefreshTokenDto);
        user.softDelete();
        userRepository.save(user);
    }

    /**
     * 내부 메소드
     */

    private void checkPasswordOrThrow(UserReq.DeleteAccountDto dto, User user) {
        if (!passwordUtil.matches(dto.password(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
        }
    }

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
