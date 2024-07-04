package com.example.test.sec11;


import com.example.models.sec11.BankServiceGrpc;
import com.example.models.sec11.WithdrawRequest;
import com.example.test.common.AbstractChannelTest;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Deadline;
import org.example.common.GrpcServer;
import org.example.sec11.repository.AccountRepository;
import org.example.sec11.repository.DeadlineBankService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class L03WaitForReadyTest extends AbstractChannelTest {

    private static final Logger log = LoggerFactory.getLogger(L03WaitForReadyTest.class);

    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;


    @BeforeAll
    public void setup() {
        //this.grpcServer.start();
        Runnable runnable = () -> {
            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
            this.grpcServer.start();
        };
        //running runnable in different thread
        Thread.ofVirtual().start(runnable);
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(this.channel);
    }

    @AfterAll
    public void stop() {
        AccountRepository.restoreDb();
        this.grpcServer.stop();
    }

    @Test
    public void blockingServerStreamingDeadlineTest() {
        log.info("sending the request");
        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();
        var iterator = this.bankBlockingStub
                .withWaitForReady()
                .withDeadline(Deadline.after(15, TimeUnit.SECONDS))
                .withdraw(request);
        while (iterator.hasNext()) {
            log.info("{}", iterator.next());
        }

    }

}
