package com.example.test.sec06;

import com.example.models.sec06.AccountBalance;
import com.example.models.sec06.DepositRequest;
import com.example.models.sec06.Money;
import com.example.models.sec06.WithdrawRequest;
import com.example.test.common.ResponseObserver;
import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class L05ClientStreamingClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L05ClientStreamingClientTest.class);

    @Test
    public void asyncClientDepositTest() {
        var responseObserver = ResponseObserver.<AccountBalance>create();
//        this.blockingStub.deposit() not allowed
        var requestObserver = this.stub.deposit(responseObserver);

        requestObserver.onNext(DepositRequest.newBuilder().setAccountNumber(5).build());

//        IntStream.rangeClosed(1, 10)
//                .boxed()
//                .map(i -> Money.newBuilder().setAmount(10).build())
//                .map(m -> DepositRequest.newBuilder().setMoney(m).build())
//                .forEach(requestObserver::onNext);

        IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Money.newBuilder().setAmount(10).build())
                .map(m -> DepositRequest.newBuilder().setMoney(m).build())
                .forEach(requestObserver::onNext);

        requestObserver.onCompleted();

        responseObserver.await();

        Assertions.assertEquals(1, responseObserver.getItems().size());
        Assertions.assertEquals(200, responseObserver.getItems().getFirst().getBalance());
        Assertions.assertNull(responseObserver.getThrowable());
    }

    @Test
    public void cancelClientByOnErrorTest() {
        var responseObserver = ResponseObserver.<AccountBalance>create();
        var requestObserver = this.stub.deposit(responseObserver);

        requestObserver.onNext(DepositRequest.newBuilder().setAccountNumber(5).build());
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        requestObserver.onError(new RuntimeException());
        responseObserver.await();
    }

}
