package com.revolut.accounts.dao;

import com.revolut.accounts.model.Account;
import com.revolut.accounts.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountDataStore implements Serializable {

    private static final long serialVersionUID = -9062535591923758202L;
    private final Map<Long, Account> dataStore;

    private static volatile AccountDataStore instance;

    private AccountDataStore() {
        if (Objects.nonNull(instance)) {
            throw new RuntimeException(Constants.EXCEPTION_MESSAGE_PRIVATE_CONSTRUCTOR_PROBHIBITED);
        }
        dataStore = new HashMap<>(32, 0.9f);
    }

    public Object readResolve() {
        return getInstance();
    }

    /**
     * @return AccountDataStore Instance.
     * @throws RuntimeException if private constructor is called using reflection api
     */
    public static synchronized AccountDataStore getInstance() {
        if (Objects.isNull(instance)) { //if there is no instance available... create new one
            synchronized (AccountDataStore.class) {
                if (Objects.isNull(instance)) instance = new AccountDataStore();
            }
        }
        return instance;
    }

    /**
     * @return Account if exists else null.
     * @throws NullPointerException if the specified id is null
     */
    public synchronized Account getAccount(Long id) {
        return dataStore.get(id);
    }

    /**
     * @return List of Accounts in the data store
     */
    public synchronized List<Account> getAllAccounts() {
        return new ArrayList<>(dataStore.values());
    }

    /**
     * @return true if account is saved successfully of false if already present.
     * @throws NullPointerException if the specified account is null
     */
    public synchronized boolean createAccount(Account account) {
        return Objects.isNull(dataStore.putIfAbsent(account.getId(), account));
    }

    /**
     * @return true if account balance is updated successfully of false if account id does not exists.
     * @throws NullPointerException if the specified id is null
     */
    public synchronized boolean updateAccount(Long id, Double balance) {
        Account account = dataStore.get(id);
        if (Objects.nonNull(account)) {
            account.setBalance(balance);
            return true;
        }
        return false;
    }

    /**
     * @return true if account is deleted successfully of false if it does not exists.
     * @throws NullPointerException if the specified id is null
     */
    public synchronized boolean deleteAccount(Long id) {
        return Objects.nonNull(dataStore.remove(id));
    }


    /**
     * Clear Data Store
     */
    public synchronized void deleteAllAccount() {
        dataStore.clear();
    }

    /**
     * @return the number of accounts in the data store.
     */
    public synchronized int count() {
        return dataStore.size();
    }
}
