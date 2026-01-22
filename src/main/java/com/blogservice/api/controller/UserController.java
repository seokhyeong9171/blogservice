package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.DupCheck;
import com.blogservice.api.dto.UserInfo;
import com.blogservice.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/email/exists")
    public ResponseEntity<DupCheck> emailExist(@RequestParam String email) {
        return ResponseEntity.ok(DupCheck.of(userService.checkEmail(email)));
    }

    @GetMapping("/nickname/exists")
    public ResponseEntity<DupCheck> nicknameExist(@RequestParam String nickname) {
        return ResponseEntity.ok(DupCheck.of(userService.checkNickname(nickname)));
    }

    @GetMapping
    public ResponseEntity<UserInfo.Response> userInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInfo.Response response = userService.getUserInfo(userPrincipal.getUserId());
        return ResponseEntity.ok(response);
    }


}
