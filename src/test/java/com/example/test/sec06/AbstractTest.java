package com.example.test.sec06;

import com.example.models.sec06.BankServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec06.BankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTest extends AbstractChannelTest {
    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub blockingStub;
    protected BankServiceGrpc.BankServiceStub stub;

    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.blockingStub = BankServiceGrpc.newBlockingStub(this.channel);
        this.stub = BankServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        this.grpcServer.stop();
    }
}
