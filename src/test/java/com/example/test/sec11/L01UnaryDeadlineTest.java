package com.example.test.sec11;


import com.example.models.sec11.AccountBalance;
import com.example.models.sec11.BalanceCheckRequest;
import com.example.test.common.ResponseObserver;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class L01UnaryDeadlineTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(L01UnaryDeadlineTest.class);

    @Test
    public void blockingDeadlineTest() {
        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(1)
                    .build();
            var response = this.bankBlockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .getAccountBalance(request);
        });
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, ex.getStatus().getCode());

    }

    @Test
    public void asyncDeadlineTest() {

        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        var response = ResponseObserver.<AccountBalance>create();
        this.bankStub
                .withDeadline(Deadline.after(6, TimeUnit.SECONDS))
                .getAccountBalance(request, response);
        response.await();

    }

}
