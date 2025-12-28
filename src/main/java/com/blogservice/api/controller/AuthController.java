package com.blogservice.api.controller;

import com.blogservice.api.domain.User;
import com.blogservice.api.exception.InvalidSigninInformation;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.response.SessionResponse;
import com.blogservice.api.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SecretKey secretKey;

    private final AuthService authService;

    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        Long userId = authService.signin(login);

        String jws = Jwts.builder().subject(String.valueOf(userId)).signWith(secretKey).compact();

        return new SessionResponse(jws);
    }
}
