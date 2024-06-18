package com.lms.grpcServer;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class GRPCErrorHandler {

    public static StatusRuntimeException handleException(Exception e) {
        return Status.INTERNAL
                .withDescription("Message: " + e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }
}
