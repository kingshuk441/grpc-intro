package com.example.test.asign01;

import com.example.model.asign01.GameResponse;
import com.example.model.asign01.GuessRequest;
import com.example.model.asign01.Result;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class GameResponseHandler implements StreamObserver<GameResponse> {
    private static final Logger log = LoggerFactory.getLogger(GameResponseHandler.class);
    private StreamObserver<GuessRequest> requestObserver;
    private final CountDownLatch latch = new CountDownLatch(1);
    private int mid;
    private int lo;
    private int hi;

    @Override
    public void onNext(GameResponse gameResponse) {
        var resultValue = gameResponse.getResult();
        if (resultValue == Result.TOO_LOW) {
            this.bs(this.mid + 1, this.hi);
        } else {
            this.bs(lo, this.mid - 1);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("correct number send by client: {}",this.mid);
        this.requestObserver.onCompleted();
        this.latch.countDown();
    }

    public void setRequestObserver(StreamObserver<GuessRequest> requestObserver) {
        this.requestObserver = requestObserver;
    }

    public void start() {
        this.hi = 100;
        this.bs(lo, hi);
    }

    private void bs(int lo, int hi) {
        this.lo = lo;
        this.hi = hi;
        this.mid = (lo + hi) / 2;
        log.info("sending value to server: {}",this.mid);
        this.requestObserver.onNext(GuessRequest.newBuilder().setGuess(mid).build());
    }

    public void await() {
        try {
            // indefinite time wait
            this.latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
