package com.example.test.asign01;

import com.example.model.asign01.GuessNumberGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.asign01.GameService;
import org.example.common.GrpcServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTest extends AbstractChannelTest {
    private final GrpcServer grpcServer = GrpcServer.create(new GameService());
    protected GuessNumberGrpc.GuessNumberStub stub;
    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.stub = GuessNumberGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        this.grpcServer.stop();
    }
}
