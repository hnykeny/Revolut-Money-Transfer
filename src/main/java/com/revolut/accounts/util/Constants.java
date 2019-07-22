package com.revolut.accounts.util;

public class Constants {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    public static final String ROUTE_ACCOUNTS = "/api/accounts";

    public static final String EXCEPTION_MESSAGE_ID_IS_NULL = "Id cannot be null";
    public static final String EXCEPTION_MESSAGE_INVALID_REQUEST = "Invalid Request";
    public static final String EXCEPTION_MESSAGE_NAME_IS_NULL = "Name cannot be null";
    public static final String EXCEPTION_MESSAGE_USER_NOT_FOUND = "User Does Not Exists";
    public static final String EXCEPTION_MESSAGE_USER_ALREADY_EXISTS = "User Already Exists";
    public static final String EXCEPTION_MESSAGE_ID_IS_NOT_VALID = "Id is not a valid number";
    public static final String EXCEPTION_MESSAGE_INSUFFICIENT_BALANCE = "Insufficient Balance";
    public static final String EXCEPTION_MESSAGE_PRIVATE_CONSTRUCTOR_PROBHIBITED = "Calling private constructor is prohibited. Use getInstance() method instead.";
    public static final String EXCEPTION_MESSAGE_BALANCE_IS_INVALID = "Balance can not be less than 0.00";
    public static final String EXCEPTION_MESSAGE_CUSTOMER_BENEFICIARY_SAME = "Customer Id and Beneficiary Id must be " +
            "different";
    public static final String EXCEPTION_MESSAGE_AMOUNT_IS_INVALID = "Amount is invalid";
    public static final String EXCEPTION_MESSAGE_CUSTOMER_IS_NULL = "Customer Id is null";
    public static final String EXCEPTION_MESSAGE_BENEFICIARY_IS_NULL = "Beneficiary Id is null";
}
