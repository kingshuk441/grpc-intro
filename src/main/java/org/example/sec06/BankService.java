package org.example.sec06;

import com.example.models.sec06.AccountBalance;
import com.example.models.sec06.AllAccountResponse;
import com.example.models.sec06.BalanceCheckRequest;
import com.example.models.sec06.BankServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.sec06.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAllAccounts(Empty request, StreamObserver<AllAccountResponse> responseObserver) {
        var allAccounts = AccountRepository.getAllAccountBalance()
                .entrySet()
                .stream()
                .map(e -> AccountBalance.newBuilder()
                        .setAccountNumber(e.getKey())
                        .setBalance(e.getValue())
                        .build()).toList();
        var allAccountBalanceResponse = AllAccountResponse.newBuilder().addAllAccounts(allAccounts).build();
        responseObserver.onNext(allAccountBalanceResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNo = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNo);
        var accountBalance = AccountBalance.newBuilder().setBalance(balance).setAccountNumber(accountNo).build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }
}
