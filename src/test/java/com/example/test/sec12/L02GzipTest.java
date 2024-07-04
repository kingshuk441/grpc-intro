package com.example.test.sec12;

import com.example.models.sec12.BalanceCheckRequest;
import com.example.test.sec12.interceptors.DeadlineInterceptor;
import com.example.test.sec12.interceptors.GzipRequestInterceptor;
import io.grpc.ClientInterceptor;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class L02GzipTest extends AbstractInterceptorTest {
    @Override
    protected List<ClientInterceptor> getInterceptors() {
        return List.of(new DeadlineInterceptor(Duration.ofSeconds(6)),new GzipRequestInterceptor());
    }

    @Test
    public void gzipTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        var response = this.bankBlockingStub.getAccountBalance(request);
    }


}
