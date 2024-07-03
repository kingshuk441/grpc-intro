package com.example.test.sec06;

import com.example.models.sec06.AccountBalance;
import com.example.models.sec06.AllAccountResponse;
import com.example.models.sec06.BalanceCheckRequest;
import com.example.test.common.ResponseObserver;
import com.google.protobuf.Empty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L03UnaryAsyncClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L03UnaryAsyncClientTest.class);

    @Test
    public void getBalanceAsyncTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1).build();
        var observer = ResponseObserver.<AccountBalance>create();
        this.bankStub.getAccountBalance(request, observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(100, observer.getItems().getFirst().getBalance());
        Assertions.assertNull(observer.getThrowable());
    }
    @Test
    public void getAllAccountBalanceAsyncTest() {
        var request = Empty.getDefaultInstance();
        var observer = ResponseObserver.<AllAccountResponse>create();
        this.bankStub.getAllAccounts(request, observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(10, observer.getItems().getFirst().getAccountsCount());
        Assertions.assertNull(observer.getThrowable());

    }

}
