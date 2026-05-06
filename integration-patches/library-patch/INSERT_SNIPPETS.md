Integration snippets — where to paste (Library Management System)

These snippets are non-invasive and assume the project already has `LogWriter` / `AsyncLogWriter` under `src/Library/util` (reuse them when present). Add these to the indicated classes: `LoginView`, `LibraryService`, `TransactionView`, or equivalent controller/service files.

1) Add logger field (add once per class)

```java
// near other fields in the class
private static final Library.util.LogWriter LOG_WRITER = new Library.util.LogWriter(
    Library.util.LogWriter.resolveLogFile(() -> System.getenv("LIBRARY_LOG_DIR"), "resources/logs")
);
```

2) Login attempts — insert after authentication result is computed

```java
try {
    LOG_WRITER.log(ok ? "INFO" : "WARN", username, clientIp, "LOGIN", ok ? "OK" : "FAIL", ok ? "" : "reason:bad_password");
} catch (Exception ignored) {}
```

3) Checkout / Borrow / Return — insert after operation completes in `LibraryService` or `TransactionView`

```java
try {
    LOG_WRITER.log("INFO", username, clientIp, "CHECKOUT", success ? "OK" : "FAIL", "bookId:" + bookId);
} catch (Exception ignored) {}
```

4) Authorization denial (403)

```java
try {
    LOG_WRITER.log("WARN", username, clientIp, "ACCESS", "403", "reason:unauthorized");
} catch (Exception ignored) {}
```

5) Admin actions

```java
try {
    LOG_WRITER.log("WARN", adminUser, adminIp, "ADMIN_" + actionName, result ? "OK" : "FAIL", "target:" + targetUser);
} catch (Exception ignored) {}
```

Notes
- Prefer using the existing `Library.util.LogWriter` when present to keep consistent behavior.
- Use existing `username` and `clientIp` variables; fallback to `"unknown"` and `"0.0.0.0"` if needed.
- After insertion, run `mvn -DskipTests=false test` and perform manual verification: attempt login/checkout and inspect `resources/logs/library_activity.log`.
