package com.revolut.accounts.exception;

import com.revolut.accounts.util.Constants;
import com.revolut.accounts.util.HttpStatus;

public class InSufficientBalanceException extends BaseException {

    private static final long serialVersionUID = -3344168691232514536L;

    public InSufficientBalanceException() {
        super(Constants.EXCEPTION_MESSAGE_INSUFFICIENT_BALANCE, HttpStatus.CONFLICT);
    }
}
