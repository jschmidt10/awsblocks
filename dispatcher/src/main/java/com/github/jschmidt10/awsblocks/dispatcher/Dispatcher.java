package com.github.jschmidt10.awsblocks.dispatcher;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * This is the landing point for AWS lambda proxy invocation. Dispatches to the appropriate handler based
 * on the path and method.
 */
public class Dispatcher implements RequestStreamHandler {

    private static final NotFoundHandler NOT_FOUND_HANDLER = new NotFoundHandler();
    private static final LambdaProxyResponse BAD_REQUEST = new LambdaProxyResponse(Http.BAD_REQUEST, "We could not parse your request. Be sure you are using a Lambda Proxy endpoint!");

    private final Collection<LambdaHandler> handlers;

    public Dispatcher(Collection<LambdaHandler> handlers) {
        Preconditions.checkNotNull(handlers, "The 'handlers' list cannot be null.");
        this.handlers = handlers;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaProxyResponse response;

        try {
            LambdaProxyRequest request = LambdaProxyRequest.parse(inputStream);
            response = handle(request);
        } catch (Exception e) {
            response = BAD_REQUEST;
        }

        response.writeJson(outputStream);
    }

    /*
     * Dispatches the request to the correct handler.
     */
    private LambdaProxyResponse handle(LambdaProxyRequest request) {
        return handlers
                .stream()
                .filter(h -> h.handlesPath(request.getPath()))
                .findFirst()
                .orElse(NOT_FOUND_HANDLER)
                .handle(request);
    }
}
