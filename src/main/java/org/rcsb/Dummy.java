package org.rcsb;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

public class Dummy {
    private static final String VERSION;

    static {
        Properties pomProperties = new Properties();
        try {
            pomProperties.load(
                    new BufferedInputStream(
                        Dummy.class.getResourceAsStream("/pom.properties")
            ));
        } catch (IOException ignored) {}
        VERSION = pomProperties.getProperty("project.version", "UNKNOWN");
    }

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send(String.format("Hello World from version %s", VERSION));
                    }
                }).build();
        server.start();
    }
}
