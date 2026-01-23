package com.blogservice.api.dto;

import com.blogservice.api.domain.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

public class Login {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Request {
        @Email(message = "올바른 이메일을 입력해 주세요.")
        private String email;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Response {
        private String jwt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class ResponseDto {
        private String jwt;
        private Cookie cookie;
        private User user;
    }
}
