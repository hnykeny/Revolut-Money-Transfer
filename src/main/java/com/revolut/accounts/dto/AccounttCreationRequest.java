package com.revolut.accounts.dto;

import lombok.Data;

@Data
public class AccounttCreationRequest {
    private Long id;
    private String name;
    private String balance;
}
