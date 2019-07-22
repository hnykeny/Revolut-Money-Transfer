package com.revolut.accounts.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    String message;
}
