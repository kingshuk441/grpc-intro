package org.example.asign02;

import com.example.model.asign02.*;
import io.grpc.stub.StreamObserver;

public class CalculationService extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public StreamObserver<Request> findSquareBiDirection(StreamObserver<Response> responseObserver) {
        return new CalculationHandler(responseObserver);
    }

    @Override
    public void findSquare(Request request, StreamObserver<Response> responseObserver) {
        var number = request.getNumber();
        ValidRequestHandler.isValidNumber(number)
                .ifPresentOrElse(responseObserver::onError, () -> giveOutput(request, responseObserver));
    }

    private void giveOutput(Request request, StreamObserver<Response> responseObserver) {
        responseObserver.onNext(Response.newBuilder()
                .setSuccessResponse(SuccessResponse.newBuilder()
                        .setResult(request.getNumber() * request.getNumber())
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
