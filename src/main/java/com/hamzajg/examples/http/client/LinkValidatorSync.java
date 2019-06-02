package com.hamzajg.examples.http.client;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class LinkValidatorSync {

    private static HttpClient httpClient;

    private static String validateLink(String link) {
        return null;
    }

    private static String esponseToString(HttpResponse<Void> response) {
        var status = response.statusCode();
        var success = status >= 200 && status <= 299;
        return String.format("%s -> %s (status: %s)", response.uri(), success, status);
    }
}
