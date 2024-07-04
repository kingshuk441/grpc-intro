package com.example.test.sec12;

import com.example.models.sec11.BankServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec12.BankService;
import org.example.sec12.repository.AccountRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbstractTest extends AbstractChannelTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractTest.class);
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
