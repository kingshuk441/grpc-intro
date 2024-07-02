package com.example.test.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractChannelTest {
    protected ManagedChannel channel;

    private static final Logger log = LoggerFactory.getLogger(AbstractChannelTest.class);

    @BeforeAll
    public void setupChannel() {
        log.info("managedChannel is starting");
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();
    }

    @AfterAll
    public void stopChannel() throws InterruptedException {
        log.info("managedChannel is stopped");
        this.channel.shutdownNow()
                .awaitTermination(5, TimeUnit.SECONDS);
        // Force shutdown if not terminated within the timeout
    }

}
