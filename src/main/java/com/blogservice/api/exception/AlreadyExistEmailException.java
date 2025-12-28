package com.blogservice.api.exception;

public class AlreadyExistEmailException extends CustomException{

    private static final String MESSAGE = "email already exist";
    private static final int STATUS_CODE = 400;

    public AlreadyExistEmailException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
