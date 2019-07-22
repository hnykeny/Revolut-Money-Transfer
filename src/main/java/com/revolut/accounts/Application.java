package com.revolut.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accounts.controller.AccountController;
import com.revolut.accounts.exception.GlobalExceptionHandler;
import com.revolut.accounts.service.AccountService;
import com.revolut.accounts.util.Constants;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(objectMapper);
    private static int serverPort = 5000;
    private static HttpServer server;

    public static void main(String[] args) throws IOException {

        if (args.length > 0) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        AccountController accountController = new AccountController(AccountService.getInstance(), objectMapper,
                globalExceptionHandler);

        server.createContext(Constants.ROUTE_ACCOUNTS, accountController::handle);

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static void stopServer() {
        server.stop(1);
    }

}
