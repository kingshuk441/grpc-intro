package com.example.test.sec06;

import com.example.models.sec06.BalanceCheckRequest;
import com.google.protobuf.Empty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L01UnaryBlockingClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L01UnaryBlockingClientTest.class);

    @Test
    public void getBalanceTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1).build();
        var balance = this.blockingStub.getAccountBalance(request);
        log.info("unary balance received: {}", balance);
        Assertions.assertEquals(100, balance.getBalance());
    }

    @Test
    public void getAllAccountBalanceTest() {
        var allAccountBalance = this.blockingStub.getAllAccounts(Empty.getDefaultInstance());
        log.info("unary all account balance received: {}", allAccountBalance);
        Assertions.assertEquals(10, allAccountBalance.getAccountsCount());
    }
}
