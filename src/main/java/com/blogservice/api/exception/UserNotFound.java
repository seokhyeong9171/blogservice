package com.blogservice.api.exception;

public class UserNotFound extends CustomException{
    private static final int STATUS_CODE = 404;
    private static final String MESSAGE = "존재하지 않는 사용자입니다.";

    public UserNotFound() {
        super(MESSAGE);
    }

    public UserNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
