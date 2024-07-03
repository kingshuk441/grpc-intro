package org.example.common;

import io.grpc.ServerBuilder;
import org.example.asign01.GameService;
import org.example.sec06.BankService;
import org.example.sec06.TransferService;
import org.example.sec07.FlowControlService;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws InterruptedException, IOException {
//        var server = ServerBuilder.forPort(6565)
//                .addService(new BankService())
//                .build();
//        server.start();
//        server.awaitTermination();
//        GrpcServer.create(6565, new BankService(), new TransferService())
//                .start()
//                .await();

//        GrpcServer.create(6565, new FlowControlService())
//                .start()
//                .await();

        GrpcServer.create(6565, new GameService())
                .start()
                .await();
    }
}
