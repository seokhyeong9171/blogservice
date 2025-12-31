package com.blogservice.api.exception;

public class InvalidPassword extends CustomException {

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "비밀번호가 올바르지 않습니다.";

    public InvalidPassword() {
        super(MESSAGE);
    }

    public InvalidPassword(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
