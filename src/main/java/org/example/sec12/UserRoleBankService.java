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

import static org.example.sec12.Constants.USER_ROLE_KEY;

public class UserRoleBankService extends BankServiceGrpc.BankServiceImplBase {


    private static final Logger log = LoggerFactory.getLogger(UserRoleBankService.class);

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNo = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNo);
        if (UserRole.STANDARD.equals(USER_ROLE_KEY.get())) {
            var fee = balance > 0 ? 1 : 0;
            AccountRepository.deductAmount(accountNo, fee);
            balance = balance - fee;
        }
        var accountBalance = AccountBalance.newBuilder().setBalance(balance).setAccountNumber(accountNo).build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

}
