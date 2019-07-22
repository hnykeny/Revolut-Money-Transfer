package com.revolut.accounts.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpStatus {
    OK(200),
    CREATED(201),
    ACCEPTED(202),

    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    CONFLICT(409),

    INTERNAL_SERVER_ERROR(500);

    private int code;
}
