# Architecture — Java-HttpClient

Current-state map. Descriptive, not aspirational; see `docs/adr/` for why.

## Shape

```
Java-HttpClient/   (Gradle, single module)
├── build.gradle    java toolchain → JDK 25; junit deps
├── gradlew(bat)    checked-in wrapper
└── src/
    ├── main/java/com/hamzajg/examples/http/client/
    │   └── LinkValidatorSync.java      synchronous link checker
    └── test/java/.../LinkValidatorSyncTest.java
                                   local HttpServer fixtures: 200/404/redirect/refused
```

## Behaviour

- Input: URLs from `gradlew run --args="..."`.
- Per URL: `HttpClient.send(GET)` → print `<url> -> <bool> (status: N)` or
  `(error: ...)`.
- Valid = final 2xx after redirects. Errors (e.g. `ConnectException`)
  print as invalid with the exception name.

## Design

- **One class, synchronous.** `java.net.http.HttpClient` (Java 11+) with
  `send()` — deliberately not `sendAsync`; scope is a teaching example.
- **Injectable client.** Package-private ctor accepts an `HttpClient` so
  tests control timeouts/redirect policy.
- **Network-free tests.** `com.sun.net.httpserver.HttpServer` serves
  fixtures on localhost — the suite runs anywhere.

## Toolchain

Gradle runs on CI's JDK 17; `java.toolchain.languageVersion = 25` selects
the compile JDK (auto-provisioned). Source uses `var` and `HttpClient`.
