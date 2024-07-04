package org.example.sec12;

import com.example.models.sec12.*;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.sec12.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {


    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNo = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNo);
        var accountBalance = AccountBalance.newBuilder().setBalance(balance).setAccountNumber(accountNo).build();
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var requiredAmount = request.getAmount();
        var balance = AccountRepository.getBalance(accountNumber);

        if (requiredAmount > balance) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        for (int i = 1; i <= requiredAmount / 10 && !Context.current().isCancelled(); i++) {
            var money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            log.info("money sent by service: {}", money);
            AccountRepository.deductAmount(accountNumber, 10);
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        log.info("streaming completed");
        responseObserver.onCompleted();
    }
}