package com.revolut.accounts.service;

import com.revolut.accounts.dao.AccountDataStore;
import com.revolut.accounts.exception.InSufficientBalanceException;
import com.revolut.accounts.exception.ResourceAlreadyExistsException;
import com.revolut.accounts.exception.ResourceNotFoundException;
import com.revolut.accounts.model.Account;
import com.revolut.accounts.util.Constants;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountService {

    private static AccountDataStore accountRepo = AccountDataStore.getInstance();

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    private static volatile AccountService instance;

    private AccountService() {
        if (Objects.nonNull(instance)) {
            LOGGER.log(Level.SEVERE, "AccountService private method invocation.");
            throw new RuntimeException(Constants.EXCEPTION_MESSAGE_PRIVATE_CONSTRUCTOR_PROBHIBITED);
        }
    }

    public Object readResolve() {
        return getInstance();
    }

    /**
     * @return AccountService Instance.
     * @throws RuntimeException if private constructor is called using reflection api
     */
    public static synchronized AccountService getInstance() {
        if (Objects.isNull(instance)) { //if there is no instance available... create new one
            synchronized (AccountService.class) {
                if (Objects.isNull(instance)) instance = new AccountService();
            }
        }
        return instance;
    }

    /**
     * @return List of Accounts in the data store
     */
    public synchronized List<Account> getAccounts() {
        return accountRepo.getAllAccounts();
    }

    /**
     * @throws ResourceNotFoundException if account with id does not exists
     * @throws NullPointerException      if the specified id is null
     */
    public synchronized Account getAccountById(Long id) throws ResourceNotFoundException {
        Account account = accountRepo.getAccount(id);
        if (Objects.isNull(account)) {
            LOGGER.info("GetAccountById : User Not Found : id=" + id);
            throw new ResourceNotFoundException(Constants.EXCEPTION_MESSAGE_USER_NOT_FOUND);
        }
        return account;
    }

    /**
     * @throws ResourceAlreadyExistsException if account with id already exists
     */
    public synchronized void addAccount(Account account) throws ResourceAlreadyExistsException {
        if (!accountRepo.createAccount(account)) {
            LOGGER.info("AddAccount : User Already Exists : account=" + account);
            throw new ResourceAlreadyExistsException(Constants.EXCEPTION_MESSAGE_USER_ALREADY_EXISTS);
        }
    }

    /**
     * @throws InSufficientBalanceException if customer account have balance less than requested amount transfer
     * @throws ResourceNotFoundException    if account with id does not exists
     * @throws NullPointerException         if the either of the specified id is null
     */
    public synchronized void transferAmount(Long customerId, Long beneficiaryId, Double amount) throws InSufficientBalanceException, ResourceNotFoundException {
        Account customer = getAccountById(customerId);
        Account beneficiary = getAccountById(beneficiaryId);
        if (customer.getBalance() >= amount) {
            accountRepo.updateAccount(customerId, customer.getBalance() - amount);
            accountRepo.updateAccount(beneficiaryId, beneficiary.getBalance() + amount);
            LOGGER.info(String.format("TransferAmount : Success : details={customerId=%d, beneficiaryId=%d, amount=%s}", customerId, beneficiaryId, amount));
        } else {
            LOGGER.info(String.format("TransferAmount : Insufficient Balance : details={customerId=%d, customerBalance=%s, beneficiaryId=%d, beneficiaryBalance=%s, amount=%s}", customerId, customer.getBalance(), beneficiaryId, beneficiary.getBalance(), amount));
            throw new InSufficientBalanceException();
        }
    }
}
