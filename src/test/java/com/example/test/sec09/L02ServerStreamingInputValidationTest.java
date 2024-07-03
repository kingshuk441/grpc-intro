package com.example.test.sec09;

import com.example.models.sec09.AccountBalance;
import com.example.models.sec09.BalanceCheckRequest;
import com.example.models.sec09.Money;
import com.example.models.sec09.WithdrawRequest;
import com.example.test.common.ResponseObserver;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class L02ServerStreamingInputValidationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(L02ServerStreamingInputValidationTest.class);

    @ParameterizedTest
    @MethodSource("testData")
    public void blockingInputServerStreamingValidationTest(WithdrawRequest request, Status.Code code) {

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var response = this.bankBlockingStub.withdraw(request).hasNext();
        });
        Assertions.assertEquals(code, ex.getStatus().getCode());
    }

    private Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(), Status.Code.INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(19).build(), Status.Code.INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(), Status.Code.FAILED_PRECONDITION)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void asyncInputServerStreamingValidationTest(WithdrawRequest request, Status.Code code) {

        var responseObserver = ResponseObserver.<Money>create();
        this.bankStub.withdraw(request, responseObserver);
        responseObserver.await();
        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());
        Assertions.assertEquals(code, ((StatusRuntimeException) responseObserver.getThrowable()).getStatus().getCode());
    }
}
