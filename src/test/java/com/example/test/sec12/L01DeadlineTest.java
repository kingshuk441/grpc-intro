package com.example.test.sec12;

import com.example.models.sec12.AccountBalance;
import com.example.models.sec12.BalanceCheckRequest;
import com.example.models.sec12.Money;
import com.example.models.sec12.WithdrawRequest;
import com.example.test.common.ResponseObserver;
import com.example.test.sec12.interceptors.DeadlineInterceptor;
import io.grpc.ClientInterceptor;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class L01DeadlineTest extends AbstractInterceptorTest {
    @Override
    protected List<ClientInterceptor> getInterceptors() {
        return List.of(new DeadlineInterceptor(Duration.ofSeconds(2)));
    }

    @Test
    public void blockingDeadlineTest() {
        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(1)
                    .build();
            var response = this.bankBlockingStub.getAccountBalance(request);
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
                .getAccountBalance(request, response);
        response.await();
        Assertions.assertTrue(response.getItems().isEmpty());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(response.getThrowable()).getCode());

    }

    @Test
    public void overrideInterceptorTest() {

        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();
        var response = ResponseObserver.<Money>create();
        this.bankStub
                .withdraw(request, response);
        response.await();
        Assertions.assertEquals(2, response.getItems().size());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(response.getThrowable()).getCode());


    }

}
