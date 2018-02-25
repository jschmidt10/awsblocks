package com.github.jschmidt10.awsblocks.dispatcher;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Handler for an unknown path.
 */
public class NotFoundHandler implements LambdaHandler {

    @Override
    public boolean handlesPath(String path) {
        return true;
    }

    @Override
    public LambdaProxyResponse handle(LambdaProxyRequest request, Context context) {
        return new LambdaProxyResponse(Http.NOT_FOUND, "Page not found");
    }
}
