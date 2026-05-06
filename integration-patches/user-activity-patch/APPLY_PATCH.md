Applying these patches to your local repo (User Activity Monitoring System)

1) Create a branch to apply the changes:

```bash
git checkout -b feature/add-monitoring-log-integration
```

2) Copy the files into the repo root, preserving paths (example using PowerShell or terminal from project root):

```bash
# from repo root
mkdir -p src/main/java/com/monitoring/util
mkdir -p src/test/java/com/monitoring/util
# copy files from the delivered patch folder into those locations
# e.g. (PowerShell)
Copy-Item "<path-to-patch>/integration-patches/user-activity/LogWriter.java" -Destination src/main/java/com/monitoring/util/
Copy-Item "<path-to-patch>/integration-patches/user-activity/MonitoringIntegrationDemo.java" -Destination src/main/java/com/monitoring/util/
Copy-Item "<path-to-patch>/integration-patches/user-activity/LogWriterTest.java" -Destination src/test/java/com/monitoring/util/
```

3) Run tests and build:

```bash
mvn -DskipTests=false test
```

4) Manual verification:
- Run the demo to write sample lines:

```bash
# after packaging or using compiled classes
java -cp target/classes com.monitoring.util.MonitoringIntegrationDemo
# Or run from IDE
```
- Check `resources/logs/user_activity.log` (or the directory from `USER_ACTIVITY_LOG_DIR` env var).

5) Commit & push:

```bash
git add src/main/java/com/monitoring/util/LogWriter.java src/main/java/com/monitoring/util/MonitoringIntegrationDemo.java src/test/java/com/monitoring/util/LogWriterTest.java
git commit -m "feat(logging): add user_activity.log integration (LogWriter + demo + test)"
git push -u origin feature/add-monitoring-log-integration
```

6) Create PR using the `PR_DRAFT_feature_add_monitoring_log_integration.md` as PR body.

Notes & next steps:
- This patch adds the logger and verification artifacts only. For full integration, add the single-line calls at request points (login handler, post/comment handler, download). I can prepare those smaller edits when you point to the exact file paths.
