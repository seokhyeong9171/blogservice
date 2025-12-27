package com.blogservice.api.controller;

import com.blogservice.api.domain.User;
import com.blogservice.api.exception.InvalidSigninInformation;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.response.SessionResponse;
import com.blogservice.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody Login login) {
        String accessToken = authService.signin(login);
        ResponseCookie cookie = ResponseCookie.from("SESSION", accessToken)
                .domain("localhost") // todo 서버 환경에 따른 분리 필요
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(60))
                .sameSite("Strict")
                .build();

        log.info(">>>>>> cookie={}", cookie);

        return ResponseEntity.ok()
                .header(SET_COOKIE, cookie.toString())
                .build();
    }
}
