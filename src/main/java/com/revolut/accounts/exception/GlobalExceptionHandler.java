package com.revolut.accounts.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accounts.dto.ErrorResponse;
import com.revolut.accounts.util.Constants;
import com.revolut.accounts.util.HttpStatus;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(Throwable throwable, HttpExchange exchange) {
        try {
            LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
            exchange.getResponseHeaders().set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            ErrorResponse response = getErrorResponse(throwable, exchange);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(response));
            responseBody.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private ErrorResponse getErrorResponse(Throwable throwable, HttpExchange exchange) throws IOException {
        ErrorResponse.ErrorResponseBuilder responseBuilder = ErrorResponse.builder();
        responseBuilder.message(throwable.getMessage());

        if (throwable instanceof BaseException) {
            BaseException exception = (BaseException) throwable;
            exchange.sendResponseHeaders(exception.getStatusCode().getCode(), 0);
        } else {
            exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), 0);
        }

        return responseBuilder.build();
    }

}
