package org.example.common;

import io.grpc.ServerBuilder;
import org.example.sec06.BankService;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws InterruptedException, IOException {
//        var server = ServerBuilder.forPort(6565)
//                .addService(new BankService())
//                .build();
//        server.start();
//        server.awaitTermination();
        GrpcServer.create(6565, new BankService())
                .start()
                .await();
    }
}
