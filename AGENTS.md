# AGENTS.md — Java-HttpClient

Guidance for AI agents (and humans) working in this repository.

## Project

A synchronous link checker built on the JDK's `java.net.http.HttpClient`
(Java 11+), from the *Java Fundamentals: HttpClient* course. Given URLs it
makes `GET` requests and prints a one-line report per link; a link is valid
on a 2xx status, redirects are followed.
Stack: Java (Gradle wrapper), JUnit tests, `com.sun.net.httpserver` for
test fixtures — no network needed.

## Commands

Verified 2026-11-13 against CI (`.github/workflows/ci.yml`) and README.

| Task | Command |
|---|---|
| Build + test | `sh gradlew build --no-daemon` (CI: JDK 17 launcher) |
| Tests only | `sh gradlew test` |
| Run demo | `sh gradlew run --args="https://example.com https://example.com/missing"` |
| Test report | `build/reports/tests/test/index.html` |

Requires: JDK to run Gradle (CI uses 17). **Compilation toolchain is JDK 25**
(`build.gradle` → `java.toolchain.languageVersion = 25`), auto-resolved.

## Architecture

```
src/main/java/com/hamzajg/examples/http/client/
  LinkValidatorSync.java    the checker: GET per URL, print status line
src/test/java/.../LinkValidatorSyncTest.java
    spins up local HttpServer; covers 200, 404, redirect, refused conn
build.gradle / settings.gradle / gradlew(bat)
```

`LinkValidatorSync` takes an `HttpClient` via a package-private constructor
— tests inject clients with custom timeouts/redirect policies. Keep that
seam.

## Conventions

- Course-exercise scope: one class does the work, tests pin behaviour.
  No frameworks, no external deps — JDK `java.net.http` only.
- Synchronous API (`send`, not `sendAsync`) — it's a link checker, keep it
  simple; async would be a different exercise.
- Test fixtures use `com.sun.net.httpserver.HttpServer` on localhost —
  tests must stay network-free.

## Landmines

- **Default branch is `master`, not `main`.**
- JDK 17 launcher / JDK 25 toolchain split (same as TDDLetsPlay) — don't
  "fix" either number.
- Redirect handling is `HttpClient.Redirect.NORMAL`-style follow — a 301→200
  counts valid; don't change semantics without updating tests.
- `build/` output exists locally — gitignored presumably; don't commit.

## Agent rules

- Branch from `master`; name branches `feat/*`, `fix/*`, `chore/*`, `docs/*`.
- Docs-only changes still go through a PR; never commit to `master` directly.
- Verify before claiming: `sh gradlew test` and report the real result.
- Keep changes minimal and consistent with surrounding code. No drive-by
  refactors.
- Update AGENTS.md / docs/ARCHITECTURE.md when the CLI contract, validity
  rule, or structure changes.
