package com.example.test.sec12.interceptors;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipRequestInterceptor implements ClientInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GzipRequestInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        log.info("gzip compression added");
        return channel.newCall(methodDescriptor,callOptions.withCompression("gzip"));
    }
}
