package com.example.test.sec06;

import com.example.models.sec06.BankServiceGrpc;
import com.example.models.sec06.TransferServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec09.BankService;
import org.example.sec06.TransferService;
import org.example.sec06.repository.AccountRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTest extends AbstractChannelTest {
    private final GrpcServer grpcServer = GrpcServer.create(new BankService(), new TransferService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankStub;
    protected TransferServiceGrpc.TransferServiceStub transferStub;
    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(this.channel);
        this.bankStub = BankServiceGrpc.newStub(this.channel);
        this.transferStub = TransferServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        AccountRepository.restoreDb();
        this.grpcServer.stop();
    }
}
