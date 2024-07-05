package com.example.test.sec12;

import com.example.models.sec12.BalanceCheckRequest;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import org.example.common.GrpcServer;
import org.example.sec12.BankService;
import org.example.sec12.interceptors.ApiKeyValidationInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.example.sec12.Constants.API_KEY;

public class L03ClientApiKeyInterceptorTest extends AbstractInterceptorTest {
    private static final Logger log = LoggerFactory.getLogger(L03ClientApiKeyInterceptorTest.class);

    @Override
    protected List<ClientInterceptor> getInterceptors() {
        return List.of(MetadataUtils.newAttachHeadersInterceptor(getApiKey())
        );
    }

    @Override
    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder -> {
            builder.addService(new BankService())
                    .intercept(new ApiKeyValidationInterceptor());
        });
    }

    @Test
    public void blockingClientApiKeyTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        var response = this.bankBlockingStub.getAccountBalance(request);
        log.info("{}", response);


    }

    private Metadata getApiKey() {
        var metaData = new Metadata();
        metaData.put(API_KEY, "bank-client-secret");
        return metaData;
    }
}
