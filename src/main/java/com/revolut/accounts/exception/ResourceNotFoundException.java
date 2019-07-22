package com.revolut.accounts.exception;

import com.revolut.accounts.util.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    private static final long serialVersionUID = -7283909449044061747L;

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
