package com.example.test.sec10;

import com.example.models.sec10.AccountBalance;
import com.example.models.sec10.BalanceCheckRequest;
import com.example.models.sec10.ErrorMessage;
import com.example.models.sec10.ValidationCode;
import com.example.test.common.ResponseObserver;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L01UnaryInputValidationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(L01UnaryInputValidationTest.class);

    @Test
    public void blockingInputValidationTrailerTest() {

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(10)
                    .build();
            var response = this.bankBlockingStub.getAccountBalance(request);
        });
        var key = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());
//        log.info("{}", ex.getTrailers().get(key).getValidationCode());

        Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(ex));
    }

    @Test
    public void asyncInputTrailerValidationTest() {
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(11)
                .build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        this.bankStub.getAccountBalance(request, responseObserver);
        responseObserver.await();
        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());
        Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(responseObserver.getThrowable()));
    }
}
