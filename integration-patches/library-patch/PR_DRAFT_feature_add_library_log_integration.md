Title: feat(logging): add library_activity.log integration (snippets + demo)

Summary:
- Add minimal, non-invasive logging snippet instructions for `library_activity.log` integration.
- Include `LibraryLogIntegrationDemo` to manually verify logging behavior.

Why:
- Enables the central Dashboard to read library events (failed logins, checkouts, unauthorized access) from `library_activity.log` without modifying core business logic.

What changed:
- Added `integration-patches/library/INSERT_SNIPPETS.md` with exact insertion snippets for login, checkout, and access denial.
- Added `integration-patches/library/patches/0001-insert-login-and-transaction-logging.patch` containing lines to paste into controllers/services.
- Added `integration-patches/library/LibraryLogIntegrationDemo.java` for manual verification.

How to test:
1. Apply snippets to the corresponding files.
2. Run the demo: `java -cp target/classes Library.util.LibraryLogIntegrationDemo`
3. Check `resources/logs/library_activity.log` (or the folder specified by `LIBRARY_LOG_DIR`).

Notes:
- Use existing `Library.util.LogWriter` when present; if not, I can add a LogWriter class similarly.
- Snippets are intentionally tiny and wrapped in try/catch so they don't affect app flow.
