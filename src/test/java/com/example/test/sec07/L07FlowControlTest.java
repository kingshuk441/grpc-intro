package com.example.test.sec07;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class L07FlowControlTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(L07FlowControlTest.class);



    @Test
    public void flowControlTest() {
        var responseObserver = new ResponseHandler();
        var requestObserver = this.stub.getMessages(responseObserver);
        responseObserver.setRequestObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }


}
