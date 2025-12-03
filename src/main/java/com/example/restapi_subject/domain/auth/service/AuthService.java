package com.example.restapi_subject.domain.auth.service;

import com.example.restapi_subject.domain.auth.dto.AuthReq;
import com.example.restapi_subject.domain.auth.dto.AuthRes;
import com.example.restapi_subject.domain.auth.repository.RefreshTokenRepository;
import com.example.restapi_subject.domain.user.domain.User;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.error.exception.CustomException;
import com.example.restapi_subject.global.error.exception.ExceptionType;
import com.example.restapi_subject.global.util.JwtUtil;
import com.example.restapi_subject.global.util.PasswordUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenStore;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public Long signUp(AuthReq.SignUpDto signUpDto) {
        validateSignUpDto(signUpDto);
        String hashedPwd = passwordUtil.hash(signUpDto.password());

        User newUser = User.create(
                signUpDto.email(),
                hashedPwd,
                signUpDto.nickname(),
                signUpDto.profileImage()
        );
        return userRepository.save(newUser).getId();
    }

    @Transactional
    public AuthRes.LoginDto login(AuthReq.LoginDto dto) {
        User user = authenticateUser(dto);
        AuthRes.TokenDto tokenDto = issueTokensAndPersist(user);
        return new AuthRes.LoginDto(user.getId(), tokenDto);
    }

    @Transactional
    public void logout(String refreshToken) {
        validateRefreshToken(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        boolean deleted = refreshTokenStore.compareAndDelete(userId, refreshToken);
        if(!deleted) throw new CustomException(ExceptionType.TOKEN_INVALID);
    }

    @Transactional
    public AuthRes.TokenDto refresh(String accessToken, String refreshToken) {
        if (accessToken == null || accessToken.isBlank()) throw new CustomException(ExceptionType.TOKEN_MISSING);
        Long userId = jwtUtil.extractUserIdAllowExpired(accessToken);
        checkRefreshTokenStolen(refreshToken, userId);
        validateRefreshToken(refreshToken);

        String newRt = rotateRefreshToken(refreshToken, userId);
        String newAt = jwtUtil.createAccessToken(userId);
        return new AuthRes.TokenDto(newAt, newRt);
    }

    public Optional<String> extractRefresh(HttpServletRequest request) {
        return extractFromHeaderXRefresh(request)
                .or(() -> extractFromCookies(request));
    }

    @Transactional
    public void deleteRefreshToken(Long userId) {
        userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));
        refreshTokenStore.delete(userId);
    }

    /**
     *  내부 메서드
     */

    private User authenticateUser(AuthReq.LoginDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));
        if (!passwordUtil.matches(dto.password(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
        }
        return user;
    }

    private AuthRes.TokenDto issueTokensAndPersist(User user) {
        String accessToken = jwtUtil.createAccessToken(user.getId());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        AuthRes.TokenDto tokenDto = new AuthRes.TokenDto(accessToken, refreshToken);

        refreshTokenStore.save(user.getId(), refreshToken);
        return tokenDto;
    }

    private String rotateRefreshToken(String refreshToken, Long userId) {
        String newRt = jwtUtil.createRefreshToken(userId);
        boolean swapped = refreshTokenStore.compareAndSet(userId, refreshToken, newRt);
        if (!swapped) {
            refreshTokenService.deleteForce(userId);
            throw new CustomException(ExceptionType.TOKEN_STOLEN);
        }
        return newRt;
    }

    private Optional<String> extractFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refreshToken".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return Optional.of(c.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> extractFromHeaderXRefresh(HttpServletRequest request) {
        String h = request.getHeader("X-Refresh-Token");
        return (h != null && !h.isBlank()) ? Optional.of(h.trim()) : Optional.empty();
    }

    private void validateSignUpDto(AuthReq.SignUpDto signUpDto) {
        if (userRepository.existsByEmail(signUpDto.email())) {
            throw new CustomException(ExceptionType.DUPLICATE_EMAIL);
        }
        if (!signUpDto.password().equals(signUpDto.passwordConfirm())) {
            throw new CustomException(ExceptionType.PASSWORD_MISMATCH);
        }
        if (userRepository.existsByNickname(signUpDto.nickname())) {
            throw new CustomException(ExceptionType.DUPLICATE_NICKNAME);
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ExceptionType.TOKEN_MISSING);
        }
        if (!jwtUtil.isValid(refreshToken)) {
            throw new CustomException(ExceptionType.TOKEN_INVALID);
        }
        if (jwtUtil.isExpired(refreshToken)) {
            deleteRefreshToken(jwtUtil.getUserId(refreshToken));
            throw new CustomException(ExceptionType.TOKEN_EXPIRED);
        }
        if (!jwtUtil.isRefresh(refreshToken)) {
            throw new CustomException(ExceptionType.TOKEN_NOT_REFRESH);
        }
    }

    private void checkRefreshTokenStolen(String refreshToken, Long userId) {
        String savedRt = refreshTokenStore.get(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.TOKEN_NOT_FOUND));
        if (!savedRt.equals(refreshToken)) {
            refreshTokenService.deleteForce(userId);
            throw new CustomException(ExceptionType.TOKEN_STOLEN);
        }
    }
}
