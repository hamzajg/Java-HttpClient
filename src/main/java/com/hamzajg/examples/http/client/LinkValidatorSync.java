package com.hamzajg.examples.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Checks links one at a time with the synchronous {@link HttpClient} API.
 *
 * <p>A link is considered valid when the server answers with a 2xx status.
 * Redirects are followed; a 3xx that is not followed, a 4xx/5xx, or a
 * connection failure is reported as invalid.
 */
public class LinkValidatorSync {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;

    public LinkValidatorSync() {
        this(HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    LinkValidatorSync(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: LinkValidatorSync <url> [url ...]");
            System.exit(2);
        }

        var validator = new LinkValidatorSync();
        for (var link : args) {
            System.out.println(validator.validateLink(link));
        }
    }

    /**
     * @return a one-line report: {@code uri -> true|false (status: N)} for an
     *         HTTP response, or {@code uri -> false (error: Type)} when the
     *         request could not be completed
     */
    public String validateLink(String link) {
        var request = HttpRequest.newBuilder(URI.create(link))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return responseToString(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return errorToString(link, e);
        } catch (IOException e) {
            return errorToString(link, e);
        }
    }

    private static String responseToString(HttpResponse<?> response) {
        var status = response.statusCode();
        var success = status >= 200 && status <= 299;
        return String.format("%s -> %s (status: %s)", response.uri(), success, status);
    }

    private static String errorToString(String link, Exception e) {
        return String.format("%s -> false (error: %s)", link, e.getClass().getSimpleName());
    }
}
