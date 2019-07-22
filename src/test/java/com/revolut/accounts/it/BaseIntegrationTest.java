package com.revolut.accounts.it;

import com.revolut.accounts.Application;
import com.revolut.accounts.dao.AccountDataStore;
import com.revolut.accounts.util.DataUtil;
import org.json.simple.parser.JSONParser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;

public abstract class BaseIntegrationTest {

    protected final static int PORT = 9080;
    protected final static String URL = "http://localhost:" + PORT + "/api/accounts";
    protected final static JSONParser jsonParser = new JSONParser();

    @Before
    public void setupData() {
        AccountDataStore.getInstance().deleteAllAccount();
        DataUtil.getTestData()
                .forEach(AccountDataStore.getInstance()::createAccount);
    }

    @BeforeClass
    public static void setupServer() throws IOException {
        Application.main(new String[]{String.valueOf(PORT)});
    }

    @AfterClass
    public static void stopServer() throws IOException {
        Application.stopServer();
    }

}
