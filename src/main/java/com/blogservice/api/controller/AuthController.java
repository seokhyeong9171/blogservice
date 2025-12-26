package com.blogservice.api.controller;

import com.blogservice.api.request.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/auth/login")
    public void login(@RequestBody Login login) {
      log.info(">>>login={}", login);
    }
}
