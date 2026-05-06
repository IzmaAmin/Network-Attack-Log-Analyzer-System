@echo off
REM Run JavaFX MainApp. Requires JDK 17+ and JavaFX SDK installed.
REM Set JAVAFX_SDK to your JavaFX SDK root, e.g. C:\javafx-sdk-20
if "%JAVAFX_SDK%"=="" (
  echo JAVAFX_SDK environment variable is not set.
  echo Please download JavaFX SDK (https://openjfx.io/) and set JAVAFX_SDK to the SDK folder.
  echo Example (PowerShell): $env:JAVAFX_SDK = 'C:\path\to\javafx-sdk-20'
  pause
  exit /b 1
)
set JAVAFX_LIB=%JAVAFX_SDK%\lib
echo Using JavaFX SDK at %JAVAFX_SDK%

echo Compiling Java sources...
javac -d out\classes src\main\java\com\example\dashboard\MainApp.java src\main\java\com\example\dashboard\controller\MainController.java src\main\java\com\example\dashboard\ui\MaliciousReportController.java src\main\java\com\example\dashboard\service\LogAnalyzer.java src\main\java\com\example\dashboard\service\LogReader.java src\main\java\com\example\dashboard\model\*.java src\main\java\com\example\dashboard\tools\*.java
if %ERRORLEVEL% neq 0 (
  echo Compilation failed.
  pause
  exit /b 1
)

echo Launching JavaFX application...
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp out\classes com.example.dashboard.MainApp
pause
