package org.example.asign02;

import com.example.model.asign02.ErrorCode;
import com.example.model.asign02.ErrorResponse;
import com.example.models.sec10.ErrorMessage;
import com.example.models.sec10.ValidationCode;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;

import java.util.Optional;

public class ValidRequestHandler {
    public static final Metadata.Key<ErrorResponse> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());

    public static Optional<StatusRuntimeException> isValidNumber(int number) {
        if (number < 2) {
            var metaData = returnMetaData(ErrorCode.BELOW_2, number);
            return Optional.of(Status.INVALID_ARGUMENT.withDescription("number < 2").asRuntimeException(metaData));
        }
        if (number > 20) {
            var metaData = returnMetaData(ErrorCode.ABOVE_20, number);
            return Optional.of(Status.INVALID_ARGUMENT.withDescription("number > 20").asRuntimeException(metaData));
        }
        return Optional.empty();
    }

    private static Metadata returnMetaData(ErrorCode code, int input) {
        var metaData = new Metadata();
        var errorMsg = ErrorResponse.newBuilder()
                .setErrorCode(code).setInput(input).build();
        var key = Metadata.Key.of("desc", Metadata.ASCII_STRING_MARSHALLER);
        metaData.put(key, code.toString());
        metaData.put(ERROR_MESSAGE_KEY, errorMsg);
        return metaData;
    }
}
