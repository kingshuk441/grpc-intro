package com.example.test.sec12;

import com.example.models.sec12.BankServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.common.GrpcServer;
import org.example.sec12.BankService;
import org.example.sec12.repository.AccountRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractInterceptorTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractTest.class);
    protected ManagedChannel channel;
    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankStub;

    protected abstract List<ClientInterceptor> getInterceptors();

    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .intercept(getInterceptors())
                .build();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(this.channel);
        this.bankStub = BankServiceGrpc.newStub(this.channel);
    }

    @AfterAll
    public void stop() {
        AccountRepository.restoreDb();
        this.grpcServer.stop();
        this.channel.shutdownNow();
    }
}
