package org.example.sec12.interceptors;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipResponseInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GzipResponseInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        log.info("gzip compression set on Server");
        serverCall.setCompression("gzip");
        return serverCallHandler.startCall(serverCall,metadata);
    }
}
