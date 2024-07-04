package com.example.test.sec11;


import com.example.models.sec11.AccountBalance;
import com.example.models.sec11.BalanceCheckRequest;
import com.example.models.sec11.Money;
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

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
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
        });

        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, ex.getStatus().getCode());
    }

    @Test
    public void asyncDeadlineTest() {

        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();
        var response = ResponseObserver.<Money>create();
        this.bankStub
                .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withdraw(request, response);
        response.await();
        Assertions.assertEquals(2,response.getItems().size());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(response.getThrowable()).getCode());

    }

}
