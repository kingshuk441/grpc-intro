package com.example.test.asign02;

import com.example.model.asign02.ErrorCode;
import com.example.model.asign02.Request;
import com.example.model.asign02.Response;
import com.example.test.common.ResponseObserver;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class Assignment02Test extends AbstractTest {
    @ParameterizedTest
    @MethodSource("testData1")
    public void blockingTest(Request request, ErrorCode code) {

        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var response = this.blockingStub.findSquare(request);
        });
        Assertions.assertEquals(code, getErrorCode(ex));
    }

    private Stream<Arguments> testData1() {
        return Stream.of(
                Arguments.of(Request.newBuilder().setNumber(-1).build(), ErrorCode.BELOW_2),
                Arguments.of(Request.newBuilder().setNumber(21).build(), ErrorCode.ABOVE_20)
        );
    }


    @ParameterizedTest
    @MethodSource("testData2")
    public void biDirectionTest(Request request, ErrorCode code, Integer result) {
        var responseObserver = ResponseObserver.<Response>create();
        var requestObserver = this.stub.findSquareBiDirection(responseObserver);
        requestObserver.onNext(request);
        requestObserver.onCompleted();
        responseObserver.await();
        if (code == null) {
            Assertions.assertEquals(1, responseObserver.getItems().size());
            Assertions.assertEquals(result, responseObserver.getItems().getFirst().getSuccessResponse().getResult());
            Assertions.assertNull(responseObserver.getThrowable());
        } else {
            Assertions.assertEquals(1, responseObserver.getItems().size());
            Assertions.assertEquals(code, responseObserver.getItems().getFirst().getErrorResponse().getErrorCode());
            Assertions.assertNull(responseObserver.getThrowable());
        }
    }

    private Stream<Arguments> testData2() {
        return Stream.of(
                Arguments.of(Request.newBuilder().setNumber(-1).build(), ErrorCode.BELOW_2, null),
                Arguments.of(Request.newBuilder().setNumber(21).build(), ErrorCode.ABOVE_20, null),
                Arguments.of(Request.newBuilder().setNumber(3).build(), null, 9)
        );
    }
}
