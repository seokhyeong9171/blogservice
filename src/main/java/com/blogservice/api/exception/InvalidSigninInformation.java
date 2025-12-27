package com.blogservice.api.exception;

public class InvalidSigninInformation extends CustomException {

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";

    public InvalidSigninInformation() {
        super(MESSAGE);
    }

    public InvalidSigninInformation(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
