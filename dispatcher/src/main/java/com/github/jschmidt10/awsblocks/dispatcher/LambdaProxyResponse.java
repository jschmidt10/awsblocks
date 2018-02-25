package com.github.jschmidt10.awsblocks.dispatcher;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates a new JSON object that is compatible as an AWS lambda proxy parse.
 */
public class LambdaProxyResponse {

    private static final Map<String, String> HEADERS = new TreeMap<>();

    static {
        HEADERS.put("Content-type", "application/json");
        HEADERS.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key");
        HEADERS.put("Access-Control-Allow-Methods", "*");
        HEADERS.put("Access-Control-Allow-Origin", "*");
    }

    @Getter
    private final int statusCode;

    @Getter
    private final Map<String, String> headers = HEADERS;

    @Getter
    private final String body;

    @Getter
    private final boolean isBase64Encoded = true;

    public LambdaProxyResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = stringify(body);
    }

    private String stringify(Object body) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectMapperHolder.getInstance().writeValue(baos, body);
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error converting " + body + " into a JSON string.");
        }
    }

    /**
     * Writes this response to the given output stream as JSON.
     *
     * @param outputStream the output stream to write this response to
     * @throws IOException If the JSON response could not be written to the output stream
     */
    public void writeJson(OutputStream outputStream) throws IOException {
        ObjectMapperHolder.getInstance().writeValue(outputStream, this);
    }
}
