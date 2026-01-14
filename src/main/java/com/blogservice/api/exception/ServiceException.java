package com.blogservice.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

}
