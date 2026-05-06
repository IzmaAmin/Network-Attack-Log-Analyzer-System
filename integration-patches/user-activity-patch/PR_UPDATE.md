PR update (to include snippet insertions)

Please add the following to the PR body or file list:

What else changed (suggested lines to add to PR body):
- Added manual insertion snippets for login, post/comment, and download handlers to facilitate emitting `user_activity.log` events.
- File: `integration-patches/user-activity/INSERT_SNIPPETS.md`
- File: `integration-patches/user-activity/patches/0001-insert-login-and-activity-logging.patch`

How to apply:
- Copy snippet additions into the existing controllers as outlined in `INSERT_SNIPPETS.md`.
- Run test/demo to verify.

This keeps the actual business code changes reviewable and minimal — we provided exact lines to paste so reviewer can easily incorporate them into the right context in their codebase.
