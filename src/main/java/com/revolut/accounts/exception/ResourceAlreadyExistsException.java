package com.revolut.accounts.exception;

import com.revolut.accounts.util.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = -2017875680586884585L;

    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
