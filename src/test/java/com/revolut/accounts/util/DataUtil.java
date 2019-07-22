package com.revolut.accounts.util;

import com.revolut.accounts.model.Account;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static List<Account> getTestData() {
        List<Account> data = new ArrayList<>(16);
        data.add(new Account(1L, "Adam", 1230.35));
        data.add(new Account(2L, "Brain", 10234.34));
        data.add(new Account(3L, "Chris", 10546.15));
        data.add(new Account(4L, "Daniel", 16760.23));
        data.add(new Account(5L, "Frank", 15000.00));
        data.add(new Account(6L, "George", 1450.43));
        data.add(new Account(7L, "Hans", 5610.58));
        data.add(new Account(8L, "Luka", 6000.50));
        data.add(new Account(9L, "Max", 76610.67));
        data.add(new Account(10L, "Niko", 1540.45));
        data.add(new Account(11L, "Pete", 1740.57));
        data.add(new Account(12L, "Sam", 1670.34));

        return data;
    }

}
