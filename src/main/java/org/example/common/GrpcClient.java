package org.example.common;

import com.example.models.sec06.BalanceCheckRequest;
import com.example.models.sec06.BankServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {
    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    public static void main(String[] args) {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();
        var stub = BankServiceGrpc.newBlockingStub(channel);
        var balance = stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(1).build());
        log.info("balance {}", balance);
    }
}
