package com.example.test.sec07;

import com.example.models.sec07.FlowControlServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec06.repository.AccountRepository;
import org.example.sec07.FlowControlService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTest extends AbstractChannelTest {
    private final GrpcServer grpcServer = GrpcServer.create(new FlowControlService());
    protected FlowControlServiceGrpc.FlowControlServiceStub stub;
    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.stub = FlowControlServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        AccountRepository.restoreDb();
        this.grpcServer.stop();
    }
}
