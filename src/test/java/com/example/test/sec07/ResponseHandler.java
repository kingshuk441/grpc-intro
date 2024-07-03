package com.example.test.sec07;

import com.example.models.sec07.Output;
import com.example.models.sec07.RequestSize;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ResponseHandler implements StreamObserver<Output> {


    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    private int size;
    private StreamObserver<RequestSize> requestObserver;


    @Override
    public void onNext(Output output) {
        this.size--;
        this.process(output);
        if (this.size == 0) {
            log.info("onNext on client ----------------------------------------------------------");
            this.request(ThreadLocalRandom.current().nextInt(1, 6));
        }


    }

    private void process(Output output) {
        log.info("received on client{}", output);
        Uninterruptibles.sleepUninterruptibly(
                ThreadLocalRandom.current().nextInt(50, 200)
                , TimeUnit.MILLISECONDS);
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("completed on client");
        this.requestObserver.onCompleted();
        this.latch.countDown();

    }

    private void request(int size) {
        log.info("requesting size {}", size);
        this.size = size;
        this.requestObserver.onNext(RequestSize.newBuilder().setSize(size).build());
    }

    public void setRequestObserver(StreamObserver<RequestSize> requestObserver) {
        this.requestObserver = requestObserver;
    }

    public void await() {
        try {
            // indefinite time wait
            this.latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        this.request(3);
    }
}
