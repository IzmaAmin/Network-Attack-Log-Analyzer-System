Applying these library integration snippets to your local Library project

1) Create a branch to apply the changes:

```bash
git checkout -b feature/add-library-log-integration
```

2) Paste insertion snippets into the appropriate files (examples):
- `src/Library/ui/LoginView.java` — after authentication
- `src/Library/service/LibraryService.java` or `src/Library/ui/TransactionView.java` — after checkout/return
- If your project files have different names, search for `login`, `checkout`, `borrow`, `return`.

3) Add the logger field to each modified class (if not already present):
```java
private static final Library.util.LogWriter LOG_WRITER = new Library.util.LogWriter(
    Library.util.LogWriter.resolveLogFile(() -> System.getenv("LIBRARY_LOG_DIR"), "resources/logs")
);
```

4) Run tests and manual verification:
```bash
# from project root
mvn -DskipTests=false test
# Run demo to write two sample lines:
java -cp target/classes Library.util.LibraryLogIntegrationDemo
# Verify output log file (default):
cat resources/logs/library_activity.log
```

5) Commit & push:
```bash
git add <modified files>
git commit -m "feat(logging): add library_activity.log snippet inserts"
git push -u origin feature/add-library-log-integration
```

6) Create PR using `PR_DRAFT_feature_add_library_log_integration.md` as the PR body.

If you want, I can prepare actual diffs that insert the snippets into specific files if you provide the exact file paths to modify; otherwise the snippets above are intentionally minimal and safe for manual insertion and review.
