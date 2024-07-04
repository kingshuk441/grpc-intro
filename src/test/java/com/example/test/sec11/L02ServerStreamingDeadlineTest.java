package com.example.test.sec11;


import com.example.models.sec11.AccountBalance;
import com.example.models.sec11.BalanceCheckRequest;
import com.example.models.sec11.WithdrawRequest;
import com.example.test.common.ResponseObserver;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class L02ServerStreamingDeadlineTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(L02ServerStreamingDeadlineTest.class);

    @Test
    public void blockingServerStreamingDeadlineTest() {

        try {
            var request = WithdrawRequest.newBuilder()
                    .setAccountNumber(1)
                    .setAmount(50)
                    .build();
            var iterator = this.bankBlockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .withdraw(request);
            while (iterator.hasNext()) {
                log.info("{}", iterator.next());
            }
        } catch (Exception e) {
            log.info("error");
        }
        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

    }

    @Test
    public void asyncDeadlineTest() {

        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        var response = ResponseObserver.<AccountBalance>create();
        this.bankStub
                .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .getAccountBalance(request, response);
        response.await();
        Assertions.assertTrue(response.getItems().isEmpty());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(response.getThrowable()).getCode());

    }

}
