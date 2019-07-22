package com.revolut.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accounts.exception.GlobalExceptionHandler;
import com.revolut.accounts.exception.InvalidRequestException;
import com.revolut.accounts.util.Constants;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;

public abstract class BaseController {

    private final ObjectMapper objectMapper;
    private final GlobalExceptionHandler exceptionHandler;

    public BaseController(ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(HttpExchange exchange) {
        try {
            execute(exchange);
        } catch (Exception e) {
            exceptionHandler.handle(e, exchange);
        }
    }

    protected abstract void execute(HttpExchange exchange) throws Exception;

    protected <T> T readRequest(InputStream is, Class<T> type) {
        try {
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_INVALID_REQUEST);
        }
    }

    protected <T> byte[] writeResponse(T response) {
        try {
            return objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_INVALID_REQUEST);
        }
    }

    protected static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }

}

