package org.example.sec12;

import io.grpc.Metadata;

public class Constants {
    public static final Metadata.Key<String> API_KEY = Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER);

}
