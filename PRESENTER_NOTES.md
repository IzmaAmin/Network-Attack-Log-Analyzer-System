Demo steps (for presenting to teacher)

Prerequisites:
- JDK 17+ installed and on PATH. (JavaFX optional for GUI)

Quick run (Windows):
1. Open a terminal in the project root.
2. Run `run_demo.bat` and follow prompts. It will:
   - compile minimal sources
   - run lightweight analyzer tests
   - print Analyzer output
   - launch an interactive console UI to inspect alerts

Manual commands (if you prefer):
- Compile: `javac -d out\classes src\main\java\com\example\dashboard\service\LogAnalyzer.java src\main\java\com\example\dashboard\model\*.java src\main\java\com\example\dashboard\tools\*.java`
- Run tests: `java -cp out\classes com.example.dashboard.tools.TestRunner`
- Analyzer output: `java -cp out\classes com.example.dashboard.tools.AnalyzerMain resources/logs`
- Interactive console: `java -cp out\classes com.example.dashboard.tools.UiSimulator resources/logs` (type the alert index then Enter to see details)
- JavaFX GUI (requires JavaFX SDK): set `JAVAFX_SDK` to your SDK path (e.g. `C:\javafx-sdk-20`), then run `run_javafx.bat`.

Talking points (2-3 lines each):
- Data structures: HashMap for risk tables, Deque as time-window queue, ArrayList for alerts—simple and teachable.
- Rules: BRUTE_FORCE (multiple failed logins), UNAUTHORIZED (403 or 'unauthorized'), FLOODING (many events by IP), ABNORMAL_BEHAVIOR (heuristic)
- Alerts: each contains timestamp, type, severity (LOW/MEDIUM/HIGH), user/ip, score and description.

If you want, I can create a short PowerPoint slide with these talking points and sample outputs, or record a short demo terminal session for you to play in class.