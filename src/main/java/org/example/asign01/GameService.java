package org.example.asign01;

import com.example.model.asign01.GameResponse;
import com.example.model.asign01.GuessNumberGrpc;
import com.example.model.asign01.GuessRequest;
import com.example.model.asign01.Result;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class GameService extends GuessNumberGrpc.GuessNumberImplBase {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private int correctNumber;
    private Random rand = new Random();

    @Override
    public StreamObserver<GuessRequest> makeGuess(StreamObserver<GameResponse> responseObserver) {
        correctNumber = rand.nextInt(100);
        log.info("correct Number : {}", correctNumber);
        return new GameHandler(responseObserver, correctNumber);
    }

    class GameHandler implements StreamObserver<GuessRequest> {
        private final StreamObserver<GameResponse> responseObserver;
        private int correctNumber;
        private int si;
        private int ei;
        private int attempt;

        public GameHandler(StreamObserver<GameResponse> responseObserver, int correctNumber) {
            this.correctNumber = correctNumber;
            this.responseObserver = responseObserver;
            this.si = 0;
            this.ei = 100;
            this.attempt = 0;
        }

        @Override
        public void onNext(GuessRequest guessRequest) {
            this.attempt++;
            GameResponse v = binarySearch(guessRequest);

            log.info("number received by service: {}", guessRequest.getGuess());
            log.info("status of number: {}", v.getResult());
            if (v.getResult() == Result.CORRECT) {
                this.onCompleted();
                return;
            }
            this.responseObserver.onNext(v);

        }

        private GameResponse binarySearch(GuessRequest guessRequest) {
            int number = guessRequest.getGuess();
            if (number == this.correctNumber) {
                return GameResponse.newBuilder().setAttempt(this.attempt)
                        .setResult(Result.CORRECT).build();
            }

            if (number < this.correctNumber) {
                si = number + 1;
                return GameResponse.newBuilder().setAttempt(this.attempt)
                        .setResult(Result.TOO_LOW).build();
            } else {
                return GameResponse.newBuilder().setAttempt(this.attempt)
                        .setResult(Result.TOO_HIGH).build();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            log.info("error {}", throwable.getMessage());
        }

        @Override
        public void onCompleted() {
            this.responseObserver.onCompleted();
        }
    }
}
