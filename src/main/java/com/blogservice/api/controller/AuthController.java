package com.blogservice.api.controller;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.request.Signup;
import com.blogservice.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    private final AuthService authService;

    @GetMapping("/auth/login")
    public String login() {
        return "로그인 페이지입니다.";
    }

    @PostMapping("/auth/signup")
    public void signup(@RequestBody Signup signup) {
        authService.signup(signup);
    }
}
