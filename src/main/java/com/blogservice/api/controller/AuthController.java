package com.blogservice.api.controller;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.request.Login;
import com.blogservice.api.request.Signup;
import com.blogservice.api.response.SessionResponse;
import com.blogservice.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    private final AuthService authService;

    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        Long userId = authService.signin(login);
        String jws = jwtProvider.generateJwtToken(userId);

        return new SessionResponse(jws);
    }

    @PostMapping("/auth/signup")
    public void signup(@RequestBody Signup signup) {
        authService.signup(signup);
    }
}
