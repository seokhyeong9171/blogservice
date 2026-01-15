package com.blogservice.api.auth;

import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.blogservice.api.exception.ErrorCode.*;

@Component
public class RefreshTokenProvider {

    @Value("${refreshtoken.expire}")
    private Long expireMs;

    public RefreshToken getRefreshToken(User user) {
        return RefreshToken.builder()
                .refreshToken(generateRefreshToken())
                .user(user)
                .expireAt(LocalDateTime.now().plusSeconds(expireMs))
                .build();
    }

    public Cookie getRefreshTokenCookie(RefreshToken refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken.getRefreshToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String getTokenFromCookies(Cookie[] cookies) {
        Cookie refreshTokenCookie =
                Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals("refreshToken"))
                        .findAny()
                        .orElseThrow(() -> new ServiceException(REFRESH_TOKEN_COOKIE_NOT_FOUND));

        return refreshTokenCookie.getValue();
    }

    public boolean validateRefreshToken(RefreshToken refreshToken) {
        return refreshToken != null && refreshToken.getExpireAt().isAfter(LocalDateTime.now());
    }
}
