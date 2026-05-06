Integration snippets — where to paste

Use these small, non-invasive snippets to add logging calls to the User Activity Monitoring System. They assume `LogWriter` is available at `com.monitoring.util.LogWriter` and that you added it via the patch in this folder.

1) Add logger field (add once per class that will emit logs)

Place near class fields, e.g., at top of `LoginController`, `AdminDashboard`, or other controllers:

```java
private static final com.monitoring.util.LogWriter LOG_WRITER = new com.monitoring.util.LogWriter(
    com.monitoring.util.LogWriter.resolveLogFile(() -> System.getenv("USER_ACTIVITY_LOG_DIR"), "resources/logs")
);
```

2) Login attempts — insert after authentication result is computed

Look for code that does authentication (method named `login`, `authenticate`, or similar) and insert after the boolean result is known:

```java
// after boolean ok (authentication result) and having username + clientIp
try {
    LOG_WRITER.log(ok ? "INFO" : "WARN", username, clientIp, "LOGIN", ok ? "OK" : "FAIL", ok ? "" : "reason:bad_password");
} catch (Exception ignored) {}
```

3) Post/comment event (insert in the handler that processes a post/comment)

```java
try {
    LOG_WRITER.log("INFO", username, clientIp, "POST_COMMENT", "OK", "length:" + (content == null ? 0 : content.length()));
} catch (Exception ignored) {}
```

4) Download event (insert in download handler)

```java
try {
    LOG_WRITER.log("INFO", username, clientIp, "DOWNLOAD", "OK", "file:" + fileName);
} catch (Exception ignored) {}
```

5) Authorization denial (add at the point where access is denied)

```java
try {
    LOG_WRITER.log("WARN", username, clientIp, "ACCESS", "403", "reason:unauthorized");
} catch (Exception ignored) {}
```

Notes & best practices
- Use existing variables for `username` and `clientIp`. If not available, set defaults `username = "unknown"` and `clientIp = "0.0.0.0"` before logging.
- Keep logging calls tiny and non-blocking; they must never throw to the caller. The `LogWriter` swallows IO exceptions by design.
- After adding snippets, run `mvn -DskipTests=false test` and perform a manual action (login/post) to verify a line was appended to `resources/logs/user_activity.log` or the folder set via `USER_ACTIVITY_LOG_DIR`.
- If you want, I can prepare exact diffs for specific files if you provide their paths.
