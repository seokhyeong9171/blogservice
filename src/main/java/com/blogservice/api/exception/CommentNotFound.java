package com.blogservice.api.exception;

public class CommentNotFound extends CustomException {

    private static final int STATUS_CODE = 404;
    private static final String MESSAGE = "존재하지 않는 댓글입니다.";

    public CommentNotFound() {
        super(MESSAGE);
    }

    public CommentNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
