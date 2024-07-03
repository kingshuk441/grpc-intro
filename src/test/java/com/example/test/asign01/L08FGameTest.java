package com.example.test.asign01;



import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class L08FGameTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L08FGameTest.class);



    @Test
    public void GameTest() {
        var responseObserver = new GameResponseHandler();
        var requestObserver = this.stub.makeGuess(responseObserver);
        responseObserver.setRequestObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }


}
