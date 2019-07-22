package com.revolut.accounts.exception;

import com.revolut.accounts.util.HttpStatus;
import lombok.Getter;

@Getter
class BaseException extends RuntimeException {

    private static final long serialVersionUID = -2055300556413915509L;
    private final HttpStatus statusCode;

    BaseException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
