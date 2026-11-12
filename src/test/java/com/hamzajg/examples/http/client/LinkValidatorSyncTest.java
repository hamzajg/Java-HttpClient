package com.hamzajg.examples.http.client;

import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static org.junit.Assert.assertTrue;

public class LinkValidatorSyncTest {

    private HttpServer server;
    private String baseUrl;

    @Before
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ok", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.createContext("/missing", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().add("Location", "/ok");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @After
    public void stopServer() {
        server.stop(0);
    }

    @Test
    public void reportsSuccessForA2xxResponse() {
        var report = new LinkValidatorSync().validateLink(baseUrl + "/ok");

        assertTrue(report, report.contains(baseUrl + "/ok"));
        assertTrue(report, report.contains("-> true"));
        assertTrue(report, report.contains("status: 200"));
    }

    @Test
    public void reportsFailureForA4xxResponse() {
        var report = new LinkValidatorSync().validateLink(baseUrl + "/missing");

        assertTrue(report, report.contains("-> false"));
        assertTrue(report, report.contains("status: 404"));
    }

    @Test
    public void followsRedirects() {
        var report = new LinkValidatorSync().validateLink(baseUrl + "/redirect");

        assertTrue(report, report.contains("-> true"));
        assertTrue(report, report.contains("status: 200"));
        // The report shows the URI that answered, not the one that redirected.
        assertTrue(report, report.contains(baseUrl + "/ok"));
    }

    @Test
    public void reportsFailureWhenTheConnectionIsRefused() throws IOException {
        int closedPort;
        try (var socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }

        var report = new LinkValidatorSync().validateLink("http://127.0.0.1:" + closedPort + "/");

        assertTrue(report, report.contains("-> false"));
        assertTrue(report, report.contains("error:"));
    }
}
