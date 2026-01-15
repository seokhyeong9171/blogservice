package com.blogservice.api.controller;

import com.blogservice.api.dto.Login;
import com.blogservice.api.dto.Signup;
import com.blogservice.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<Login.Response> login(HttpServletResponse servletResponse, @RequestBody @Validated Login.Request request) {
        Login.ResponseDto responseDto = authService.login(request);
        String jwt = responseDto.getJwt();
        servletResponse.setHeader(AUTHORIZATION, jwt);
        servletResponse.addCookie(responseDto.getCookie());

        // todo
        //  refresh token 생성 로직

        return ResponseEntity.ok(Login.Response.builder().jwt(jwt).build());
    }

}
