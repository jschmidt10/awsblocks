package com.github.jschmidt10.awsblocks.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Static holder for a single per-JVM ObjectMapper.
 */
public class ObjectMapperHolder {
    private static final ObjectMapper instance = new ObjectMapper();

    /**
     * Gets the initialized ObjectMapper.
     *
     * @return object mapper
     */
    public static ObjectMapper getInstance() {
        return instance;
    }
}
