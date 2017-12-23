package com.github.jschmidt10.awsblocks.dispatcher;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LambdaProxyRequestTest {

    @Test
    public void shouldParseRequestJson() throws IOException {
        LambdaProxyRequest req = LambdaProxyRequest.parse(classpathResource("request.json"));

        assertThat(req.getHttpMethod(), is("POST"));
        assertThat(req.getPath(), is("hello/world"));
    }

    private InputStream classpathResource(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }
}
