package com.github.jschmidt10.awsblocks.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DispatcherTest {

    private Dispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = new Dispatcher(Collections.singleton(new HelloWorldHandler()));
    }

    @Test
    public void shouldRespondToHandledPath() throws Exception {
        InputStream input = toProxyRequest("hello", "GET", "");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        dispatcher.handleRequest(input, output, null);

        JsonNode node = readResponse(output);

        assertThat(node.get("statusCode").asInt(), is(Http.OK));
        assertThat(node.get("body").asText(), is("\"world\""));
    }

    @Test
    public void shouldReturnServerErrorOnExceptionInHandler() throws Exception {
        // We specified POST methods to throw an Exception in the HelloWorldHandler
        InputStream input = toProxyRequest("hello", "POST", "");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        dispatcher.handleRequest(input, output, null);

        JsonNode node = readResponse(output);

        assertThat(node.get("statusCode").asInt(), is(Http.INTERNAL_ERROR));
    }

    @Test
    public void shouldReturnBadRequestOnExceptionParsingRequest() throws Exception {
        InputStream input = new ByteArrayInputStream("{\"name\":\"invalid request\"}".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        dispatcher.handleRequest(input, output, null);

        JsonNode node = readResponse(output);

        assertThat(node.get("statusCode").asInt(), is(Http.BAD_REQUEST));
    }

    @Test
    public void shouldReturnNotFoundOnUnhandledPath() throws Exception {
        InputStream input = toProxyRequest("undefinedPath", "POST", "");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        dispatcher.handleRequest(input, output, null);

        JsonNode node = readResponse(output);

        assertThat(node.get("statusCode").asInt(), is(Http.NOT_FOUND));
    }

    private JsonNode readResponse(ByteArrayOutputStream baos) throws IOException {
        return ObjectMapperHolder.getInstance().readValue(baos.toByteArray(), JsonNode.class);
    }

    private InputStream toProxyRequest(String path, String method, String body) throws Exception {
        JsonNodeFactory nf = ObjectMapperHolder.getInstance().getNodeFactory();

        ObjectNode pathParamsNode = nf.objectNode();
        pathParamsNode.put("proxy", path);

        ObjectNode root = nf.objectNode();
        root.put("httpMethod", method);
        root.put("body", body);
        root.put("pathParameters", pathParamsNode);

        byte[] bytes = ObjectMapperHolder.getInstance().writeValueAsBytes(root);
        return new ByteArrayInputStream(bytes);
    }

    /*
     * A handler that will reply to requests for '/hello' with 'world'.
     */
    private static class HelloWorldHandler implements LambdaHandler {

        @Override
        public boolean handlesPath(String path) {
            return path.equals("hello");
        }

        @Override
        public LambdaProxyResponse handle(LambdaProxyRequest request) {
            if (request.getHttpMethod().equals("POST")) {
                throw new IllegalArgumentException("HelloWorldHandler does not support POST");
            }

            return new LambdaProxyResponse(Http.OK, "world");
        }
    }
}
