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
    PASSWORD_NOT_MATCHING(BAD_REQUEST, "password not matching"),

    POST_NOT_FOUND(NOT_FOUND, "post not found"),

    REFRESH_TOKEN_COOKIE_NOT_FOUND(BAD_REQUEST, "refresh token cookie not found"),
    REFRESH_TOKEN_INVALID(BAD_REQUEST, "refresh token invalid"),
    TOKEN_LIST_EMPTY(NOT_FOUND, "token list is empty"),
    POST_AUTHOR_NOT_MATCHING(BAD_REQUEST, "post author not matching"),
    POST_DELETED(BAD_REQUEST, "post deleted"),;

    private final HttpStatus status;
    private final String message;

}
