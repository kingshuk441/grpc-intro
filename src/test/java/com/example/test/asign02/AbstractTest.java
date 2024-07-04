package com.example.test.asign02;


import com.example.model.asign02.CalculatorServiceGrpc;
import com.example.model.asign02.ErrorCode;
import com.example.model.asign02.ErrorResponse;
import com.example.models.sec10.ValidationCode;
import com.example.test.common.AbstractChannelTest;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.example.asign02.CalculationService;
import org.example.common.GrpcServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;


public class AbstractTest extends AbstractChannelTest {
    private static final Metadata.Key<ErrorResponse> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
    private final GrpcServer grpcServer = GrpcServer.create(new CalculationService());
    protected CalculatorServiceGrpc.CalculatorServiceStub stub;
    protected CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub;

    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.blockingStub = CalculatorServiceGrpc.newBlockingStub(this.channel);
        this.stub = CalculatorServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        this.grpcServer.stop();
    }

    protected ErrorCode getErrorCode(Throwable throwable) {

        return Optional.ofNullable(Status.trailersFromThrowable(throwable))
                .map(m -> m.get(ERROR_MESSAGE_KEY))
                .map(ErrorResponse::getErrorCode)
                .orElseThrow();

    }
}
