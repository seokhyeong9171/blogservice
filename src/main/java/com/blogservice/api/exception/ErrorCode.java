package com.blogservice.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_DUPLICATED(CONFLICT, "this email already exists"),
    NICKNAME_DUPLICATED(CONFLICT, "this nickname already exists"),

    USER_NOT_FOUND(BAD_REQUEST, "user not found"),
    PASSWORD_NOT_MATCHING(BAD_REQUEST, "password not matching"),;

    private final HttpStatus status;
    private final String message;

}
