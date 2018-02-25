package com.github.jschmidt10.awsblocks.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper for a raw JSON object that knows how to extract Lambda proxy request information.
 */
@AllArgsConstructor
public class LambdaProxyRequest {

    /**
     * Factory method for creating a new LambdaProxyRequest from the request input stream.
     *
     * @param inputStream
     * @return request
     * @throws IOException if an error occurs while reading the request
     */
    public static LambdaProxyRequest parse(InputStream inputStream) throws IOException {
        JsonNode node = ObjectMapperHolder.getInstance().readValue(inputStream, JsonNode.class);

        String path = node.get("pathParameters").get("proxy").asText();
        String httpMethod = node.get("httpMethod").asText();
        String body = node.get("body").asText();

        return new LambdaProxyRequest(path, httpMethod, body);
    }

    @Getter
    private final String path;

    @Getter
    private final String httpMethod;

    @Getter
    private final String body;
}
