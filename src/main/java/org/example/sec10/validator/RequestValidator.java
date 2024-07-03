package org.example.sec10.validator;

import com.example.models.sec10.ErrorMessage;
import com.example.models.sec10.ValidationCode;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;

import java.util.Optional;

public class RequestValidator {
    private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

    public static Optional<StatusRuntimeException> validateAccount(int accountNumber) {
        if (accountNumber > 0 && accountNumber < 11) {
            return Optional.empty();
        }
        var metaData = toMetaData(ValidationCode.INVALID_ACCOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.withDescription("account number should be b/w 1 and 10")
                .asRuntimeException(metaData));
    }

    public static Optional<StatusRuntimeException> isAmountDivisibleBy10(int amount) {
        if (amount > 0 && amount % 10 == 0) {
            return Optional.empty();
        }
        var metaData = toMetaData(ValidationCode.INVALID_AMOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.withDescription("requested amount should be multiple of 10")
                .asRuntimeException(metaData));
    }

    public static Optional<StatusRuntimeException> hasSufficientBalance(int amount, int balance) {
        if (amount <= balance) {
            return Optional.empty();
        }
        var metaData = toMetaData(ValidationCode.INSUFFICIENT_BALANCE);
        return Optional.of(Status.INVALID_ARGUMENT.withDescription("balance is less than amount")
                .asRuntimeException(metaData));
    }

    private static Metadata toMetaData(ValidationCode code) {
        var metaData = new Metadata();
//        var key = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());
        var errorMsg = ErrorMessage.newBuilder()
                .setValidationCode(code).build();
        var key = Metadata.Key.of("desc", Metadata.ASCII_STRING_MARSHALLER);
        metaData.put(key, code.toString());
        metaData.put(ERROR_MESSAGE_KEY, errorMsg);
        return metaData;
    }
}
