package org.example.asign02;

import com.example.model.asign02.ErrorResponse;
import com.example.model.asign02.Request;
import com.example.model.asign02.Response;
import com.example.model.asign02.SuccessResponse;
import io.grpc.stub.StreamObserver;

import static org.example.asign02.ValidRequestHandler.ERROR_MESSAGE_KEY;

public class CalculationHandler implements StreamObserver<Request> {
    private final StreamObserver<Response> responseObserver;


    public CalculationHandler(StreamObserver<Response> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(Request request) {
        var status = ValidRequestHandler.isValidNumber(request.getNumber());


        if (status.isEmpty()) {
            this.responseObserver.onNext(Response.newBuilder()
                    .setSuccessResponse(
                            SuccessResponse.newBuilder()
                                    .setResult(request.getNumber() * request.getNumber())
                                    .build())
                    .build()
            );
        } else {
            responseObserver.onNext(Response.newBuilder()
                    .setErrorResponse(ErrorResponse
                            .newBuilder()
                            .setErrorCode(status.get().getTrailers().get(ERROR_MESSAGE_KEY).getErrorCode())
                            .setInput(request.getNumber())
                            .build())
                    .build());
        }

    }

    @Override
    public void onError(Throwable throwable) {
        this.responseObserver.onError(throwable);
    }

    @Override
    public void onCompleted() {
        this.responseObserver.onCompleted();
        ;
    }
}
