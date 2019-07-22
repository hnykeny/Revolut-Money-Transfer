package com.revolut.accounts.exception;

import com.revolut.accounts.util.HttpStatus;

public class InvalidRequestException extends BaseException {

    private static final long serialVersionUID = -3616669394402610387L;

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
