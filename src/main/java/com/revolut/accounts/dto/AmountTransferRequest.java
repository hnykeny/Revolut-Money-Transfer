package com.revolut.accounts.dto;

import lombok.Data;

@Data
public class AmountTransferRequest {
    private Long customerId;
    private Long beneficiaryId;
    private String amount;
}
