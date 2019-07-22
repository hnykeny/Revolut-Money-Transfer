package com.revolut.accounts.ut;

import com.revolut.accounts.dao.AccountDataStore;
import com.revolut.accounts.exception.InSufficientBalanceException;
import com.revolut.accounts.exception.ResourceAlreadyExistsException;
import com.revolut.accounts.exception.ResourceNotFoundException;
import com.revolut.accounts.model.Account;
import com.revolut.accounts.service.AccountService;
import com.revolut.accounts.util.DataUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServiceTest {

    private AccountService accountService = AccountService.getInstance();

    @Before
    public void setupData() {
        AccountDataStore.getInstance().deleteAllAccount();
        DataUtil.getTestData()
                .forEach(AccountDataStore.getInstance()::createAccount);
    }

    // Method: getAccounts
    @Test
    public void when_getAccounts_Return_AllAccounts() {

        List<Account> expectedAccounts = DataUtil.getTestData();

        // When
        List<Account> accounts = accountService.getAccounts();


        //Then
        assertEquals(expectedAccounts.size(), accounts.size());
        assertTrue(accounts.containsAll(expectedAccounts));
    }

    // Method: getAccountById
    @Test
    public void when_getAccountById_WithValidId_ReturnAccount() {
        //When
        Account account = accountService.getAccountById(5L);

        //Then
        assertEquals(5L, (long) account.getId());
        assertEquals("Frank", account.getName());
        assertEquals(15000.00D, (double) account.getBalance(), 0);
    }

    // Method: getAccountById
    @Test(expected = ResourceNotFoundException.class)
    public void when_getAccountById_WithInvalidId_Throw() {
        //When
        Account account = accountService.getAccountById(15L);
    }


    // Method: addAccount
    @Test(expected = ResourceAlreadyExistsException.class)
    public void when_addAccount_WithConflictingId_Throw() {
        //Given
        Account account = new Account(5L, "Karl", 23.34D);

        // When
        accountService.addAccount(account);
    }

    // Method: addAccount
    @Test
    public void when_addAccount_WithValidAccount_succeed() {
        //Given
        Account account = new Account(15L, "John", 2300.35D);

        // When
        accountService.addAccount(account);

        // Then
        assertEquals(13, accountService.getAccounts().size());
        assertTrue(accountService.getAccounts().contains(account));
    }

    // Method: transferAmount
    @Test
    public void when_transferAmount_WithValidParameters_succeed() {
        // When | Customer balance: 15000 | Beneficiary balance: 6000.50
        accountService.transferAmount(5L, 8L, 2500D);

        // Then
        assertEquals(12500.0D, accountService.getAccountById(5L).getBalance(), 0);
        assertEquals(8500.50D, accountService.getAccountById(8L).getBalance(), 0);
    }

    // Method: transferAmount
    @Test(expected = ResourceNotFoundException.class)
    public void when_transferAmount_WithInvalidCustomerId_throw() {
        // When | Customer balance: 15000 | Beneficiary balance: 6000.50
        accountService.transferAmount(15L, 8L, 2500D);
    }

    // Method: transferAmount
    @Test(expected = ResourceNotFoundException.class)
    public void when_transferAmount_WithInvalidBeneficiaryId_throw() {
        // When | Customer balance: 15000 | Beneficiary balance: 6000.50
        accountService.transferAmount(5L, 18L, 2500D);
    }

    // Method: transferAmount
    @Test(expected = InSufficientBalanceException.class)
    public void when_transferAmount_WithAmountGreaterThanCustomerBalance_throw() {
        // When | Customer balance: 15000 | Beneficiary balance: 6000.50
        accountService.transferAmount(5L, 8L, 25000D);
    }


}
