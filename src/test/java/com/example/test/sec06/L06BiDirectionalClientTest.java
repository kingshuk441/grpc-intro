package com.example.test.sec06;

import com.example.models.sec06.*;
import com.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class L06BiDirectionalClientTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L06BiDirectionalClientTest.class);

    @Test
    public void asyncTransferTest() {
        var responseObserver = ResponseObserver.<TransferResponse>create();
        var requestObserver = this.transferStub.transfer(responseObserver);
        var requests = List.of(
                TransferRequest.newBuilder().setFromAccount(6).setToAccount(6).setAmount(10).build(),
                TransferRequest.newBuilder().setFromAccount(6).setToAccount(7).setAmount(110).build(),
                TransferRequest.newBuilder().setFromAccount(6).setToAccount(7).setAmount(10).build(),
                TransferRequest.newBuilder().setFromAccount(7).setToAccount(6).setAmount(10).build()
        );
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();

        responseObserver.await();
        Assertions.assertEquals(4,responseObserver.getItems().size());
        this.validate(responseObserver.getItems().getFirst(), TransferStatus.REJECTED, 100, 100);
        this.validate(responseObserver.getItems().get(1), TransferStatus.REJECTED, 100, 100);
        this.validate(responseObserver.getItems().get(2), TransferStatus.COMPLETED, 90, 110);
        this.validate(responseObserver.getItems().get(3), TransferStatus.COMPLETED, 100, 100);

    }

    private void validate(TransferResponse response, TransferStatus status, int fromAccountBalance, int toAccountBalance) {
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(fromAccountBalance, response.getFromAccount().getBalance());
        Assertions.assertEquals(toAccountBalance, response.getToAccount().getBalance());
    }

}
