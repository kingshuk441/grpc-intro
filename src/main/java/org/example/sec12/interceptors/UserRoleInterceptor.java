package org.example.sec12.interceptors;

import io.grpc.*;
import org.example.sec12.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

import static org.example.sec12.Constants.*;
/*
We have only getAccountBalance feature
user-token-1, user-token-2 => prime users, return the balance as it is
user-token-3, user-token-4 => standard users, deduct $1 and then return the balance
any other token            => not valid...!
*/
public class UserRoleInterceptor implements ServerInterceptor {
    private static final Set<String> PRIME_SET = Set.of("user-token-1", "user-token-2");
    private static final Set<String> STANDARD_SET = Set.of("user-token-3", "user-token-4");
    private static final Logger log = LoggerFactory.getLogger(UserRole.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        var token = extractToken(metadata.get(USER_TOKEN_KEY));
        log.info("token {}", token);
        var ctx = toContext(token);
        if (Objects.nonNull(ctx)) {
            return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
        }

        return close(serverCall, metadata, Status.UNAUTHENTICATED.withDescription("token is either null or invalid"));
    }

    private Context toContext(String token) {
        if (Objects.nonNull(token) && (PRIME_SET.contains(token) || STANDARD_SET.contains(token))) {
            var role = PRIME_SET.contains(token) ? UserRole.PRIME : UserRole.STANDARD;
            //returning new ctx
            return Context.current().withValue(USER_ROLE_KEY, role);
        }
        return null;
    }

    private String extractToken(String value) {
        return Objects.nonNull(value) && value.startsWith(BEARER) ? value.substring(BEARER.length()).trim() : null;
    }

    private boolean isValid(String token) {
        return Objects.nonNull(token) && (PRIME_SET.contains(token) || STANDARD_SET.contains(token));
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(ServerCall<ReqT, RespT> serverCall, Metadata metadata, Status status) {
        serverCall.close(status, metadata);
        return new ServerCall.Listener<ReqT>() {
        };
    }
}
