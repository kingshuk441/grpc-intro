package com.example.test.sec09;

import com.example.models.sec09.BankServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec09.repository.AccountRepository;
import org.example.sec09.BankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTest extends AbstractChannelTest {
    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankStub;
    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(this.channel);
        this.bankStub = BankServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        AccountRepository.restoreDb();
        this.grpcServer.stop();
    }
}
