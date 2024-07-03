package com.example.test.sec06;

import com.example.models.sec06.AccountBalance;
import com.example.models.sec06.BalanceCheckRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class L02UnaryAsyncClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L02UnaryAsyncClientTest.class);

    @Test
    public void getBalanceAsyncTest() throws InterruptedException {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1).build();
        var latch = new CountDownLatch(1);
        this.bankStub.getAccountBalance(request, new StreamObserver<AccountBalance>() {
            @Override
            public void onNext(AccountBalance accountBalance) {
                log.info("account balance async {}", accountBalance);
                Assertions.assertEquals(100, accountBalance.getBalance());
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
        latch.await();

    }

}
