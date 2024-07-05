package com.example.test.sec12;

import com.example.models.sec12.AccountBalance;
import com.example.models.sec12.BalanceCheckRequest;
import com.example.test.common.ResponseObserver;
import io.grpc.CallCredentials;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import org.example.common.GrpcServer;
import org.example.sec12.BankService;
import org.example.sec12.interceptors.UserTokenInterceptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.example.sec12.Constants.BEARER;
import static org.example.sec12.Constants.USER_TOKEN_KEY;

public class L04UserSessionTokenInterceptorTest extends AbstractInterceptorTest {
    private static final Logger log = LoggerFactory.getLogger(L04UserSessionTokenInterceptorTest.class);

    @Override
    protected List<ClientInterceptor> getInterceptors() {
        return Collections.emptyList();
    }

    @Override
    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder -> {
            builder.addService(new BankService())
                    .intercept(new UserTokenInterceptor());
        });
    }

    @Test
    public void unaryUserCredentialTest() {
        for (int i = 1; i <= 5; i++) {
            var req = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
            var res = this.bankBlockingStub
                    .withCallCredentials(new UserSessionToken("user-token-" + i))
                    .getAccountBalance(req);
            log.info("{}", res);
        }
    }

    @Test
    public void streamingUserCredentialTest() {
        for (int i = 1; i <= 5; i++) {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();
            var response = ResponseObserver.<AccountBalance>create();
            this.bankStub
                    .withCallCredentials(new UserSessionToken("user-token-" + i))
                    .getAccountBalance(request, response);
            response.await();
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
