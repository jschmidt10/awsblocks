package com.github.jschmidt10.awsblocks.dispatcher;

/**
 * Handles a specific Lambda request.
 */
public interface LambdaHandler {

    /**
     * Identifies if this path can be handled by this handler.
     *
     * @param path the request resource path
     * @return true if the handler accepts this path, false, otherwise.
     */
    boolean handlesPath(String path);

    /**
     * Handles the request.
     *
     * @param request a lambda proxy request
     */
    LambdaProxyResponse handle(LambdaProxyRequest request);
}
