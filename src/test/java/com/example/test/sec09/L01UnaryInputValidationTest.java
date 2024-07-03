package com.example.test.sec09;

import com.example.models.sec09.AccountBalance;
import com.example.models.sec09.BalanceCheckRequest;
import com.example.test.common.ResponseObserver;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L01UnaryInputValidationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(L01UnaryInputValidationTest.class);

    @Test
    public void blockingInputValidationTest() {
//        var request = BalanceCheckRequest.newBuilder()
//                .setAccountNumber(11)
//                .build();
//        try {
//            var response = this.bankBlockingStub.getAccountBalance(request);
//        } catch (StatusRuntimeException e) {
//            log.info("{}", e.getStatus().getCode());
//        }

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(11)
                    .build();
            var response = this.bankBlockingStub.getAccountBalance(request);
        });
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
    }

    @Test
    public void asyncInputValidationTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(11)
                .build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        this.bankStub.getAccountBalance(request, responseObserver);
        responseObserver.await();
        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, ((StatusRuntimeException) responseObserver.getThrowable()).getStatus().getCode());
    }
}
