package org.example.sec10;

import com.example.models.sec10.*;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.sec10.repository.AccountRepository;
import org.example.sec10.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BankService.class);


    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
//                .map(Status::asException)//casting in runtimeException.
                .ifPresentOrElse(responseObserver::onError,
                        () -> sendAccountBalance(request, responseObserver));

    }

    private void sendAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNo = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNo);
        var accountBalance = AccountBalance.newBuilder().setBalance(balance).setAccountNumber(accountNo).build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .or(() -> RequestValidator.isAmountDivisibleBy10(request.getAmount()))
                .or(() -> RequestValidator.hasSufficientBalance(request.getAmount(), AccountRepository.getBalance(request.getAccountNumber())))
//                .map(Status::asRuntimeException)
                .ifPresentOrElse(responseObserver::onError,
                        () -> sendMoney(request, responseObserver));
        ;
    }

    private void sendMoney(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        try {
            var accountNumber = request.getAccountNumber();
            var requiredAmount = request.getAmount();
            var balance = AccountRepository.getBalance(accountNumber);

            if (requiredAmount > balance) {
                responseObserver.onCompleted();
                return;
            }

            for (int i = 1; i <= requiredAmount / 10; i++) {
                var money = Money.newBuilder().setAmount(10).build();
                if (i == 3) {
                    throw new RuntimeException("forced server error");
                }
                responseObserver.onNext(money);
                log.info("money sent by service: {}", money);
                AccountRepository.deductAmount(accountNumber, 10);
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }

    }


}
