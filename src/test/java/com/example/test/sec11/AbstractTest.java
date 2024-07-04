package com.example.test.sec11;

import com.example.models.sec11.BankServiceGrpc;
import com.example.test.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.sec11.repository.AccountRepository;
import org.example.sec11.repository.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbstractTest extends AbstractChannelTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
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
