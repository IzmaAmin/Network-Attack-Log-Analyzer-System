@echo off
cd /d "%~dp0"
echo Compiling sources...
javac -d out\classes src\main\java\com\example\dashboard\service\LogAnalyzer.java src\main\java\com\example\dashboard\model\LogEntry.java src\main\java\com\example\dashboard\model\AlertEntry.java src\main\java\com\example\dashboard\model\Report.java src\main\java\com\example\dashboard\tools\TestRunner.java src\main\java\com\example\dashboard\tools\AnalyzerMain.java src\main\java\com\example\dashboard\tools\UiSimulator.java 2>nul
if %ERRORLEVEL% neq 0 (
  echo Compilation failed; please ensure JDK is available.
  pause
  exit /b 1
)
echo Running analyzer unit tests...
java -cp out\classes com.example.dashboard.tools.TestRunner
echo.
echo Analyzer output:
java -cp out\classes com.example.dashboard.tools.AnalyzerMain resources/logs
echo.
echo Launching interactive console UI (type 0,1 or q to view alerts and quit)...
java -cp out\classes com.example.dashboard.tools.UiSimulator resources/logs
echo Demo complete.
pause
