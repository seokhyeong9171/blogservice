package com.blogservice.api.controller;

import com.blogservice.api.dto.Signup;
import com.blogservice.api.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
