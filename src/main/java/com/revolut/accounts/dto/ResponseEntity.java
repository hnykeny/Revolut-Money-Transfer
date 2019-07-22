package com.revolut.accounts.dto;

import com.revolut.accounts.util.HttpStatus;
import com.sun.net.httpserver.Headers;
import lombok.Value;

@Value
public class ResponseEntity<T> {

    private final T body;
    private final Headers headers;
    private final HttpStatus statusCode;
}