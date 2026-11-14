# ADR 0001 — Initial architecture

Date: 2026-11-13 (baseline recorded retroactively)
Status: accepted (describes what is built)

## Context

A course exercise (*Java Fundamentals: HttpClient*) demonstrating the JDK
11+ `java.net.http` client through a small link checker.

## Decisions

1. **Synchronous `HttpClient.send`, not async.** One URL at a time,
   blocking — the example teaches the client API, not concurrency.
   Rationale: KISS; async streams would double the lesson's surface.
2. **JDK-only, zero dependencies.** `java.net.http.HttpClient` +
   `com.sun.net.httpserver` for tests. Rationale: course constraint — the
   JDK is the subject.
3. **Package-private constructor injection.** `LinkValidatorSync(HttpClient)`
   lets tests substitute timeout/redirect-policy clients without changing
   the public API. Rationale: testability without a DI framework.
4. **Validity = final 2xx after redirects.** Redirects are followed; the
   report shows the terminal status. Rationale: matches how users
   experience links.
5. **Gradle wrapper + JDK 25 toolchain.** Checked-in `gradlew`; compilation
   targets Java 25 while CI launches Gradle on JDK 17. Rationale: fleet
   SDK standardisation; toolchain auto-resolution keeps contributors free
   of manual JDK installs.
6. **Tests use local HttpServer fixtures.** 200, 404, redirect, refused
   connection — no external network. Rationale: hermetic CI.

## Consequences

- CLI contract is `gradlew run --args="<urls..."` — changing arg handling
  breaks the documented usage.
- "Valid" is defined by HTTP semantics only; no content checks.
