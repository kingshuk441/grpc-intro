package org.example.sec06.requestHandlers;

import com.example.models.sec06.AccountBalance;
import com.example.models.sec06.TransferRequest;
import com.example.models.sec06.TransferResponse;
import com.example.models.sec06.TransferStatus;
import io.grpc.stub.StreamObserver;
import org.example.sec06.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferRequestHandler implements StreamObserver<TransferRequest> {
    private static final Logger log = LoggerFactory.getLogger(TransferRequestHandler.class);
    private final StreamObserver<TransferResponse> responseObserver;

    public TransferRequestHandler(StreamObserver<TransferResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        var status = this.transfer(transferRequest);
//        if (status == TransferStatus.COMPLETED) { // 1 request does not require 1 response every time
        var response = TransferResponse.newBuilder()
                .setStatus(status)
                .setFromAccount(this.updatedAccountBalance(transferRequest.getFromAccount()))
                .setToAccount(this.updatedAccountBalance(transferRequest.getToAccount()))
                .build();
        responseObserver.onNext(response);
//        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("client error : {}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        log.info("transfer request stream completed on client");
        this.responseObserver.onCompleted();
    }

    private TransferStatus transfer(TransferRequest request) {
        var amount = request.getAmount();
        var fromAccount = request.getFromAccount();
        var toAccount = request.getToAccount();
        var status = TransferStatus.REJECTED;
        if (AccountRepository.getBalance(fromAccount) >= amount && (fromAccount != toAccount)) {
            AccountRepository.addAmount(toAccount, amount);
            AccountRepository.deductAmount(fromAccount, amount);
            status = TransferStatus.COMPLETED;
        }
        return status;
    }

    private AccountBalance updatedAccountBalance(int accountNumber) {
        return AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber)).build();
    }
}
