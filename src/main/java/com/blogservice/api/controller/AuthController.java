package com.blogservice.api.controller;

import com.blogservice.api.auth.RefreshTokenProvider;
import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.Login;
import com.blogservice.api.dto.ReIssue;
import com.blogservice.api.dto.Signup;
import com.blogservice.api.service.AuthService;
import com.blogservice.api.service.SnapshotService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SnapshotService snapshotService;
    private final RefreshTokenProvider refreshTokenProvider;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Signup.Response> signup(@RequestBody @Validated Signup.Request request) {
        Long registeredUser = authService.signup(request);
        return ResponseEntity.status(CREATED)
                .body(Signup.Response.builder().userId(registeredUser).build());
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Login.Response> login(
            HttpServletRequest servletRequest, HttpServletResponse servletResponse,
            @RequestBody @Validated Login.Request request) {

        Login.ResponseDto responseDto = authService.login(request);

        String jwt = responseDto.getJwt();
        servletResponse.setHeader(AUTHORIZATION, "Bearer " + jwt);
        servletResponse.addCookie(responseDto.getCookie());

        snapshotService.logLogin(servletRequest, responseDto.getUser());

        return ResponseEntity.ok(Login.Response.builder().jwt(jwt).build());
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<ReIssue> refresh(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest servletRequest, HttpServletResponse servletResponse
    ) {

        String refreshToken = refreshTokenProvider.getTokenFromCookies(servletRequest.getCookies());

        String jwt = authService.reissueToken(userPrincipal.getUserId(), refreshToken);
        servletResponse.setHeader(AUTHORIZATION, "Bearer " + jwt);

        return ResponseEntity.ok(ReIssue.builder().jwt(jwt).build());
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        String refreshTokenFromCookie = refreshTokenProvider.getTokenFromCookies(servletRequest.getCookies());
        authService.logout(refreshTokenFromCookie);

        servletResponse.setHeader(AUTHORIZATION, null);
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        servletResponse.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

}
