package com.blogservice.api.exception;

public class PostNotFound extends CustomException {

    private static final int STATUS_CODE = 404;
    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
