package com.revolut.accounts.exception;

import com.revolut.accounts.util.HttpStatus;

public class MethodNotAllowedException extends BaseException {

    private static final long serialVersionUID = -1604442304800846120L;

    public MethodNotAllowedException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
