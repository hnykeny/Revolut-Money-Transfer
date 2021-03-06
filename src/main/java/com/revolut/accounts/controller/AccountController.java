package com.revolut.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accounts.dto.AccounttCreationRequest;
import com.revolut.accounts.dto.AmountTransferRequest;
import com.revolut.accounts.dto.ResponseEntity;
import com.revolut.accounts.exception.GlobalExceptionHandler;
import com.revolut.accounts.exception.InvalidRequestException;
import com.revolut.accounts.exception.MethodNotAllowedException;
import com.revolut.accounts.model.Account;
import com.revolut.accounts.service.AccountService;
import com.revolut.accounts.util.ApiUtils;
import com.revolut.accounts.util.Constants;
import com.revolut.accounts.util.HttpStatus;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class AccountController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(AccountController.class.getName());

    private AccountService accountService;

    public AccountController(AccountService accountService, ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws Exception {
        byte[] response;
        ResponseEntity responseEntity;
        if ("GET".equals(exchange.getRequestMethod())) {
            String[] pathTokens = exchange.getRequestURI().toString().split("/");
            if (pathTokens.length == 4 && !pathTokens[3].isBlank())
                responseEntity = getAccount(exchange, pathTokens[3]);
            else if (pathTokens.length < 4)
                responseEntity = getAllAccounts();
            else
                throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_INVALID_REQUEST);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().toString().equals(Constants.ROUTE_ACCOUNTS_TRANSFER))
                responseEntity = transferAmount(exchange.getRequestBody());
            else
                responseEntity = createAccount(exchange.getRequestBody());
        } else {
            LOGGER.info(exchange.getRequestMethod() + " Request on URI:" + exchange.getRequestURI());
            throw new MethodNotAllowedException("Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI());
        }

        exchange.getResponseHeaders().putAll(responseEntity.getHeaders());
        exchange.sendResponseHeaders(responseEntity.getStatusCode().getCode(), 0);
        response = super.writeResponse(responseEntity.getBody());

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity<Collection<Account>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAccounts(),
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), HttpStatus.OK);
    }

    private ResponseEntity<Account> getAccount(HttpExchange exchange, String idStr) {
        try {
            Account account = accountService.getAccountById(Long.parseLong(idStr));

            return new ResponseEntity<Account>(account,
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), HttpStatus.OK);
        } catch (NumberFormatException e) {
            LOGGER.info("EXCEPTION: " + exchange.getRequestMethod() + " - " + exchange.getRequestURI() + " : Id is not valid");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_ID_IS_NOT_VALID);
        }
    }

    private ResponseEntity<String> createAccount(InputStream is) {
        AccounttCreationRequest accounttCreationRequest = super.readRequest(is, AccounttCreationRequest.class);
        Double balance = 0.00d;
        if (Objects.isNull(accounttCreationRequest.getId())) {
            LOGGER.info("EXCEPTION: CreateAccount : Id is null");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_ID_IS_NULL);
        }
        if (Objects.isNull(accounttCreationRequest.getName()) || accounttCreationRequest.getName().isBlank()) {
            LOGGER.info("EXCEPTION: CreateAccount : Name is null or invalid");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_NAME_IS_NULL);
        }
        if (Objects.isNull(accounttCreationRequest.getBalance())) {
            LOGGER.info("CreateAccount : Balance is null, defaults to 0.00");
        } else if (Double.parseDouble(accounttCreationRequest.getBalance()) < 0) {
            LOGGER.info("EXCEPTION: CreateAccount : Balance is invalid");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_BALANCE_IS_INVALID);
        } else {
            balance = Double.parseDouble(accounttCreationRequest.getBalance());
        }
        accountService.addAccount(new Account(accounttCreationRequest.getId(),
                accounttCreationRequest.getName(), balance));

        return new ResponseEntity<>("",
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), HttpStatus.CREATED);
    }

    private ResponseEntity<String> transferAmount(InputStream is) {
        AmountTransferRequest amountTransferRequest = super.readRequest(is, AmountTransferRequest.class);
        if (Objects.isNull(amountTransferRequest.getCustomerId())) {
            LOGGER.info("EXCEPTION: TransferAmount : Customer Id is null");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_CUSTOMER_IS_NULL);
        }
        if (Objects.isNull(amountTransferRequest.getBeneficiaryId())) {
            LOGGER.info("EXCEPTION: TransferAmount : Beneficiary Id is null");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_BENEFICIARY_IS_NULL);
        }
        if (amountTransferRequest.getCustomerId().longValue() == amountTransferRequest.getBeneficiaryId().longValue()) {
            LOGGER.info("EXCEPTION: TransferAmount : Customer Id and Beneficiary Id is same");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_CUSTOMER_BENEFICIARY_SAME);
        }
        if (Objects.isNull(amountTransferRequest.getAmount()) || Double.parseDouble(amountTransferRequest.getAmount()) <= 0) {
            LOGGER.info("EXCEPTION: TransferAmount : Amount is invalid");
            throw new InvalidRequestException(Constants.EXCEPTION_MESSAGE_AMOUNT_IS_INVALID);
        }
        accountService.transferAmount(amountTransferRequest.getCustomerId(),
                amountTransferRequest.getBeneficiaryId(),
                Double.parseDouble(amountTransferRequest.getAmount()));

        return new ResponseEntity<>("",
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), HttpStatus.ACCEPTED);
    }
}
