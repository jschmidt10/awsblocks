package com.github.jschmidt10.awsblocks.dispatcher;

/**
 * Handler for an unknown path.
 */
public class NotFoundHandler implements LambdaHandler {

    @Override
    public boolean handlesPath(String path) {
        return true;
    }

    @Override
    public LambdaProxyResponse handle(LambdaProxyRequest request) {
        return new LambdaProxyResponse(Http.NOT_FOUND, "Page not found");
    }
}
