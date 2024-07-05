package com.example.test.sec12;

import com.example.models.sec12.AccountBalance;
import com.example.models.sec12.BalanceCheckRequest;
import com.example.test.common.ResponseObserver;
import io.grpc.CallCredentials;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import org.example.common.GrpcServer;

import org.example.sec12.UserRoleBankService;
import org.example.sec12.interceptors.UserRoleInterceptor;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.example.sec12.Constants.BEARER;
import static org.example.sec12.Constants.USER_TOKEN_KEY;

public class L05UserRoleContextTest extends AbstractInterceptorTest {
    private static final Logger log = LoggerFactory.getLogger(L05UserRoleContextTest.class);

    @Override
    protected List<ClientInterceptor> getInterceptors() {
        return Collections.emptyList();
    }

    @Override
    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder -> {
            builder.addService(new UserRoleBankService())
                    .intercept(new UserRoleInterceptor());
        });
    }

    @RepeatedTest(5)
    public void unaryUserCredentialTest() {
        for (int i = 1; i <= 4; i++) {
            var req = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
            var res = this.bankBlockingStub
                    .withCallCredentials(new UserSessionToken("user-token-" + i))
                    .getAccountBalance(req);
            log.info("{}", res);
        }
    }

    private static class UserSessionToken extends CallCredentials {
        private final String jwt;
        private static final String TOKEN_FORMAT = "%s %s";

        private UserSessionToken(String jwt) {
            this.jwt = jwt;
        }

        @Override
        public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
            executor.execute(() -> {
                var metaData = new Metadata();
                metaData.put(USER_TOKEN_KEY, TOKEN_FORMAT.formatted(BEARER, jwt));
                metadataApplier.apply(metaData);
            });


        }
    }

}
