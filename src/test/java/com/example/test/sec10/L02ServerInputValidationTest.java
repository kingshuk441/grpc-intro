package com.example.test.sec10;

import com.example.models.sec10.Money;
import com.example.models.sec10.WithdrawRequest;
import com.example.models.sec10.AccountBalance;
import com.example.models.sec10.BalanceCheckRequest;
import com.example.models.sec10.ErrorMessage;
import com.example.models.sec10.ValidationCode;
import com.example.test.common.ResponseObserver;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class L02ServerInputValidationTest extends AbstractTest {


    private static final Logger log = LoggerFactory.getLogger(L02ServerInputValidationTest.class);

    @ParameterizedTest
    @MethodSource("testData1")
    public void blockingInputServerStreamingValidationTest(WithdrawRequest request, ValidationCode code) {

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var response = this.bankBlockingStub.withdraw(request).hasNext();
        });
        Assertions.assertEquals(code, getValidationCode(ex));
    }

    private Stream<Arguments> testData1() {
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(), ValidationCode.INVALID_ACCOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(19).build(), ValidationCode.INVALID_AMOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(), ValidationCode.INSUFFICIENT_BALANCE)
        );
    }

    @ParameterizedTest
    @MethodSource("testData1")
    public void asyncInputServerStreamingValidationTest(WithdrawRequest request, ValidationCode code) {

        var responseObserver = ResponseObserver.<Money>create();
        this.bankStub.withdraw(request, responseObserver);
        responseObserver.await();
        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());
        Assertions.assertEquals(code, getValidationCode(responseObserver.getThrowable()));
    }
}
