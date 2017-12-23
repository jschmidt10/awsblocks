package com.github.jschmidt10.awsblocks.dispatcher;

/**
 * Holds needed http response codes.
 */
public class Http {

    private Http() {
    }

    public static final int OK = 200;

    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;

    public static final int INTERNAL_ERROR = 500;
}
