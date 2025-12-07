package com.example.restapi_subject.global.util;

import com.example.restapi_subject.domain.auth.dto.AuthRes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenResponseWriter {

    private final CookieUtil cookieUtil;

    /** AT + RT 모두 응답에 세팅 */
    public void writeAuthTokens(HttpServletResponse response, AuthRes.TokenDto tokenDto) {

        // Access Token
        response.setHeader("Authorization", "Bearer " + tokenDto.accessToken());
        response.addHeader("Access-Control-Expose-Headers", "Authorization");

        // Refresh Token Cookie
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshCookie(tokenDto.refreshToken()).toString()
        );
    }

    /** RT 삭제 */
    public void clearRefreshToken(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.clearRefreshCookie().toString());
    }
}
