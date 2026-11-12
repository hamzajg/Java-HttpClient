# Java-HttpClient

A synchronous link checker built on the JDK's `java.net.http.HttpClient`
(Java 11+), from the *Java Fundamentals: HttpClient* course.

Given one or more URLs it makes a `GET` request for each and prints a one-line
report:

```
https://example.com -> true (status: 200)
https://example.com/missing -> false (status: 404)
http://127.0.0.1:1/ -> false (error: ConnectException)
```

A link counts as valid when the server answers with a 2xx status. Redirects are
followed, so a 301/302 that ends in a 200 is valid.

## Requirements

- JDK 17 (the code uses `var` and the Flow-free `HttpClient`; Java 11+ works)
- No Gradle install needed — the wrapper is checked in

## Run it

```bash
sh gradlew run --args="https://example.com https://example.com/missing"
```

## Test it

```bash
sh gradlew test
```

The tests start a local `com.sun.net.httpserver.HttpServer` and cover a 200, a
404, a redirect, and a refused connection, so they need no network access. The
HTML report is written to `build/reports/tests/test/index.html`.

## Layout

```
src/main/java/com/hamzajg/examples/http/client/LinkValidatorSync.java
src/test/java/com/hamzajg/examples/http/client/LinkValidatorSyncTest.java
```

`LinkValidatorSync` accepts an `HttpClient` in a package-private constructor so
tests can inject a client with different timeouts or a redirect policy.

## License

See [LICENSE](LICENSE).
