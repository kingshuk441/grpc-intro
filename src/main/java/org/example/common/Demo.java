package org.example.common;

import org.example.asign01.GameService;
import org.example.asign02.CalculationService;
import org.example.sec10.BankService;

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

//        GrpcServer.create(6565, new GameService())
//                .start()
//                .await();

//        GrpcServer.create(6565, new BankService())
//                .start()
//                .await();

        GrpcServer.create(6565, new CalculationService())
                .start()
                .await();
    }
}
