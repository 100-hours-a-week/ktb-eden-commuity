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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTestMocking {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordUtil passwordUtil;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
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
        when(passwordUtil.hash("Test1234!")).thenReturn("HASHED_PW");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
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
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.withId(1L);
        });

        //when
        authService.signUp(dto1);

        //then
        assertThatThrownBy(() -> authService.signUp(dto2))
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
        assertThatThrownBy(() -> authService.signUp(dto))
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
        when(userRepository.existsByNickname(dto1.nickname()))
                .thenReturn(false)
                .thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.withId(u.getId());
        });

        //when
        authService.signUp(dto1);

        //then
        assertThatThrownBy(() -> authService.signUp(dto2))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.DUPLICATE_NICKNAME.getErrorMessage());
        Mockito.verify(userRepository, times(2)).existsByNickname(dto1.nickname());
        Mockito.verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    @DisplayName("로그인 성공-AuthRes.LoginDto를 반환")
    void login_success() throws Exception {
        //given
        AuthReq.LoginDto dto = new AuthReq.LoginDto("ok@test.com", "Test1234!");

        User mockUser = createMockUser();

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(mockUser));
        when(passwordUtil.matches(dto.password(), mockUser.getPassword()))
                .thenReturn(true);
        when(jwtUtil.createAccessToken(1L)).thenReturn("newAT");
        when(jwtUtil.createRefreshToken(1L)).thenReturn("newRT");

        Mockito.doNothing().when(refreshTokenRepository).save(1L,"newRT");

        //when
        AuthRes.LoginDto result = authService.login(dto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.tokenDto().accessToken()).isEqualTo("newAT");
        assertThat(result.tokenDto().refreshToken()).isEqualTo("newRT");

        Mockito.verify(userRepository, times(1)).findByEmail(dto.email());
        Mockito.verify(passwordUtil, times(1)).matches(dto.password(), mockUser.getPassword());
        Mockito.verify(jwtUtil, times(1)).createAccessToken(1L);
        Mockito.verify(jwtUtil, times(1)).createRefreshToken(1L);
        Mockito.verify(refreshTokenRepository, times(1)).save(1L,"newRT");
    }

    @Test
    @DisplayName("로그인 실패 - USER_NOT_FOUND")
    void login_fail_user_not_found() throws Exception {
        //given
        AuthReq.LoginDto dto = new AuthReq.LoginDto("non@test.com", "pw");

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        //when, then
       assertThatThrownBy(() -> authService.login(dto))
               .isInstanceOf(CustomException.class)
               .hasMessageContaining(ExceptionType.USER_NOT_FOUND.getErrorMessage());
    }

    @Test
    @DisplayName("로그인 실패 - INVALID_CREDENTIALS")
    void login_fail_invalid_credentials() throws Exception {
        //given
        AuthReq.LoginDto dto = new AuthReq.LoginDto("ok@test.com", "wrong!pw");
        //when
        User user = createMockUser();

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(user));
        when(passwordUtil.matches(dto.password(), user.getPassword()))
                .thenReturn(false);
        //then
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.INVALID_CREDENTIALS.getErrorMessage());
    }

    @Test
    @DisplayName("로그아웃 성공 - refreshToken 정상 삭제")
    void logout_success() throws Exception {
        //given
        String refreshToken = "refreshToken";
        Long userId = 1L;

        when(jwtUtil.isValid(refreshToken)).thenReturn(true);
        when(jwtUtil.isExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.isRefresh(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserId(refreshToken)).thenReturn(userId);

        when(refreshTokenRepository.compareAndDelete(userId, refreshToken)).thenReturn(true);
        //when, then
        assertThatCode(() -> authService.logout(refreshToken))
                .doesNotThrowAnyException();

        Mockito.verify(jwtUtil, times(1)).getUserId(refreshToken);
        Mockito.verify(refreshTokenRepository, times(1)).compareAndDelete(userId, refreshToken);
    }
    
    @Test
    @DisplayName("로그아웃 실패 - TOKEN_MISSING(refreshToken == null)")
    void logout_fail_token_missing() throws Exception {
        assertThatThrownBy(() -> authService.logout(null))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_MISSING.getErrorMessage());
        Mockito.verify(refreshTokenRepository, never()).compareAndDelete(anyLong(), anyString());
    }
    
    @Test
    @DisplayName("로그아웃 실패 - TOKEN_INVALID(refreshToken 형식 오류)")
    void logout_fail_token_invalid() throws Exception {
        //given
        String refreshToken = "invalid";
        //when
        when(jwtUtil.isValid(refreshToken)).thenReturn(false);
        //then
        assertThatThrownBy(() -> authService.logout(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_INVALID.getErrorMessage());

        Mockito.verify(jwtUtil, times(1)).isValid(refreshToken);
        Mockito.verify(jwtUtil, never()).getUserId(anyString());
        Mockito.verify(refreshTokenRepository, never()).compareAndDelete(anyLong(), anyString());
    }
    
    @Test
    @DisplayName("로그아웃 실패 - TOKEN_EXPIRED(jwtUitl.isExpired == true)")
    void logout_fail_token_expired() throws Exception {
        //given
        String refreshToken = "expired";
        Long userId = 1L;
        //when
        Mockito.when(jwtUtil.isValid(refreshToken)).thenReturn(true);
        when(jwtUtil.isExpired(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserId(refreshToken)).thenReturn(userId);

        User user = createMockUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.logout(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_EXPIRED.getErrorMessage());
        //then
        Mockito.verify(userRepository, times(1)).findById(userId);
        Mockito.verify(refreshTokenRepository).delete(userId);
    }

    @Test
    @DisplayName("로그아웃 실패 - TOKEN_NOT_REFRESH(jwtUitl.isRefresh == false)")
    void logout_fail_not_refresh() throws Exception {
        //given
        String refreshToken = "notRefresh";
        Long userId = 1L;
        //when
        Mockito.when(jwtUtil.isValid(refreshToken)).thenReturn(true);
        when(jwtUtil.isExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.isRefresh(refreshToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.logout(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_NOT_REFRESH.getErrorMessage());
        //then
        Mockito.verify(jwtUtil, times(1)).isValid(refreshToken);
    }


    @Test
    @DisplayName("토큰 갱신 성공 - 새 AT,RT 발급")
    void refresh_success() throws Exception {
        //given
        String at = "expiredAT";
        String rt = "oldRT";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.of("oldRT"));

        when(jwtUtil.isValid(rt)).thenReturn(true);
        when(jwtUtil.isExpired(rt)).thenReturn(false);
        when(jwtUtil.isRefresh(rt)).thenReturn(true);

        when(jwtUtil.createRefreshToken(userId)).thenReturn("newRT");
        when(refreshTokenRepository.compareAndSet(userId, rt, "newRT")).thenReturn(true);

        when(jwtUtil.createAccessToken(userId)).thenReturn("newAT");

        //when
        AuthRes.TokenDto result = authService.refresh(at, rt);
        //then
        assertThat(result.accessToken()).isEqualTo("newAT");
        assertThat(result.refreshToken()).isEqualTo("newRT");

        verify(refreshTokenRepository).compareAndSet(userId, rt, "newRT");
        verify(jwtUtil).createAccessToken(userId);
    }
    
    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_MISSING ")
    void refresh_fail_token_missing_() throws Exception {
        //then
        assertThatThrownBy(() -> authService.refresh(null, "rt"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_MISSING.getErrorMessage());
    }
    
    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_NOT_FOUND")
    void refresh_fail_token_not_found() throws Exception {
        //given
        String at = "at";
        String rt = "rt";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.empty());
        //when, then
        assertThatThrownBy(() -> authService.refresh(at, rt))
                        .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_NOT_FOUND.getErrorMessage());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_STOLEN")
    void refresh_fail_token_stolen() throws Exception {
        //given
        String at = "at";
        String rt = "hackerRT";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.of("realRT"));
        //when, then
        assertThatThrownBy(() -> authService.refresh(at, rt))
                        .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_STOLEN.getErrorMessage());

        verify(refreshTokenService).deleteForce(userId);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_INVALID")
    void refresh_fail_token_invalid() throws Exception {
        //given
        String at = "at";
        String rt = "rt";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.of(rt));

        when(jwtUtil.isValid(rt)).thenReturn(false);
        //when, then
        assertThatThrownBy(() -> authService.refresh(at, rt))
                        .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_INVALID.getErrorMessage());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_EXPIRED")
    void refresh_fail_token_expired() throws Exception {
        //given
        String at = "at";
        String rt = "expiredRt";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.of(rt));

        when(jwtUtil.isValid(rt)).thenReturn(true);
        when(jwtUtil.isExpired(rt)).thenReturn(true);
        when(jwtUtil.getUserId(rt)).thenReturn(userId);
        User mockUser = createMockUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        //when, then
        assertThatThrownBy(() -> authService.refresh(at, rt))
                        .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_EXPIRED.getErrorMessage());

        verify(refreshTokenRepository).delete(userId);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - TOKEN_NOT_REFRESH")
    void refresh_fail_token_not_refresh() throws Exception {
        //given
        String at = "at";
        String rt = "notRefreshRt";
        Long userId = 1L;

        when(jwtUtil.extractUserIdAllowExpired(at)).thenReturn(userId);
        when(refreshTokenRepository.get(userId)).thenReturn(Optional.of(rt));

        when(jwtUtil.isValid(rt)).thenReturn(true);
        when(jwtUtil.isExpired(rt)).thenReturn(false);
        when(jwtUtil.isRefresh(rt)).thenReturn(false);

        //when, then
        assertThatThrownBy(() -> authService.refresh(at, rt))
                        .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_NOT_REFRESH.getErrorMessage());

        verify(userRepository, never()).findById(userId);
    }

    @Test
    @DisplayName("토큰 추출 성공 - X-Refresh-Token 헤더")
    void extractRefresh_from_header_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Refresh-Token")).thenReturn("headerRT");

        Optional<String> result = authService.extractRefresh(request);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("headerRT");
    }

    @Test
    @DisplayName("토큰 추출 성공 - 쿠키")
    void extractRefresh_from_cookie_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        Cookie[] cookies = { new Cookie("refreshToken", "cookieRT") };
        when(request.getHeader("X-Refresh-Token")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);

        Optional<String> result = authService.extractRefresh(request);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("cookieRT");
    }

    @Test
    @DisplayName("토큰 empty - 헤더와 쿠키 모두 없으면 empty")
    void extractRefresh_empty() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Refresh-Token")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        Optional<String> result = authService.extractRefresh(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractRefresh - X-Refresh-Token 헤더가 빈문자열이면 empty")
    void extractRefresh_header_blank() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Refresh-Token")).thenReturn("   "); // 공백
        when(request.getCookies()).thenReturn(null);

        Optional<String> result = authService.extractRefresh(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractRefresh - 쿠키는 있지만 refreshToken 이름이 없으면 empty")
    void extractRefresh_cookie_but_no_refreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        Cookie[] cookies = {
                new Cookie("sessionId", "123"),
                new Cookie("accessToken", "xxx")
        };

        when(request.getHeader("X-Refresh-Token")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);

        Optional<String> result = authService.extractRefresh(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("rotateRefreshToken 성공 - swapped == true")
    void rotateRefreshToken_success() throws Exception {
        // given
        Long userId = 1L;
        String oldRt = "oldRt";

        when(jwtUtil.createRefreshToken(userId)).thenReturn("newRt");
        when(refreshTokenRepository.compareAndSet(userId, oldRt, "newRt"))
                .thenReturn(true);

        Method method = AuthService.class.getDeclaredMethod("rotateRefreshToken", String.class, Long.class);
        method.setAccessible(true);

        String result = (String) method.invoke(authService, oldRt, userId);

        // then
        assertThat(result).isEqualTo("newRt");
    }

    @Test
    @DisplayName("rotateRefreshToken 실패 - swapped == false → TOKEN_STOLEN")
    void rotateRefreshToken_fail_swapped_false() throws Exception {
        // given
        Long userId = 1L;
        String oldRt = "oldRt";

        when(jwtUtil.createRefreshToken(userId)).thenReturn("newRt");
        when(refreshTokenRepository.compareAndSet(userId, oldRt, "newRt"))
                .thenReturn(false);

        Method method = AuthService.class.getDeclaredMethod("rotateRefreshToken", String.class, Long.class);
        method.setAccessible(true);

        assertThatThrownBy(() -> method.invoke(authService, oldRt, userId))
                .cause()
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionType.TOKEN_STOLEN.getErrorMessage());

        verify(refreshTokenService).deleteForce(userId);
    }

    private static User createMockUser() {
        User user = User.builder()
                .id(1L)
                .email("ok@test.com")
                .password("HASHED_PW")
                .nickname("eden")
                .profileImage("img")
                .deleted(false)
                .build();
        return user;
    }
}