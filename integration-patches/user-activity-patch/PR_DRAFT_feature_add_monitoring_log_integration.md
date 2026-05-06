Title: feat(logging): add user_activity.log integration (LogWriter + demo + tests)

Summary:
- Add a minimal, non-invasive file logger `LogWriter` that writes structured events to `user_activity.log`.
- Add `MonitoringIntegrationDemo` (manual run) and `LogWriterTest` (JUnit) to validate behavior.

Why:
- Enables the central Dashboard to read user activity events from a shared log file without changing app business logic.

What changed:
- Added `src/main/java/com/monitoring/util/LogWriter.java`
- Added `src/main/java/com/monitoring/util/MonitoringIntegrationDemo.java`
- Added `src/test/java/com/monitoring/util/LogWriterTest.java`
- Documentation/instructions included in `integration-patches/user-activity/APPLY_PATCH.md`

How to test:
1. Set env `USER_ACTIVITY_LOG_DIR` to a folder, or use default `resources/logs`.
2. Run the demo: `java -cp target/classes com.monitoring.util.MonitoringIntegrationDemo`
3. Verify `user_activity.log` contains sample lines.
4. Run tests: `mvn -DskipTests=false test`.

Notes:
- Logger truncates details to 512 chars and swallows IO exceptions to avoid affecting app flow.
- Next: add small log calls at key handlers (login, post_comment, download). I can prepare patches for those specific files on request.
