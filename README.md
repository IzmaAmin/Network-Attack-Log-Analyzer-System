🧩 Integration Patch & Module Documentation
📌 Patch Overview

This repository includes a small integration patch for the DS Lab Dashboard project.
The patch extends the dashboard with a malicious activity monitoring and alert visualization module without modifying existing core functionality.

It is designed to be non-invasive, modular, and easily pluggable into an existing JavaFX dashboard system.

⚙️ What the Patch Contains
MaliciousReportController.java
→ UI controller for displaying security alerts and detailed logs
malicious_report.fxml
→ JavaFX view containing:
ListView (alerts list)
TextArea (alert details)
🔗 Integration Instructions
1️⃣ Copy Backend Monitoring Classes

Move the following classes from the monitoring project:

com.monitoring.forwarder.client

Into dashboard project:

com.example.dashboard.integration

Included files:

LogForwarderClient.java
MaliciousActivityDetector.java
2️⃣ Add UI Components
Place MaliciousReportController.java into:
com.example.dashboard.ui
Add malicious_report.fxml into:
resources/fxml/
3️⃣ Connect to Main Application

In MainController or MainApp:

Create an instance of LogForwarderClient
Configure it to poll:
http://localhost:<port>/entries
On receiving new logs:
Run MaliciousActivityDetector.analyze()
Forward alerts to:
MaliciousReportController.showAlerts()
4️⃣ Configuration

You can adjust:

Polling interval
Detection thresholds
Risk scoring rules

via application properties file.

🧠 System Design Insight

This patch introduces a lightweight SIEM-like extension into the dashboard system.

It follows this flow:

Log Forwarder Client
        ↓
Log Stream (HTTP /entries)
        ↓
Malicious Activity Detector
        ↓
Alert Generator
        ↓
JavaFX UI Controller
        ↓
Dashboard View (FXML)
🧱 Data Structures Used (DS Lab Focus)

This module is designed specifically for Data Structures & Algorithms lab evaluation.

📊 Used Structures:
HashMap
riskByIp
riskByUser
→ Tracks risk scoring per entity
Queue / Deque
→ Maintains time-windowed activity logs
ArrayList
→ Stores and returns alert objects for UI rendering
🚨 Detection Rules Implemented

The system uses rule-based detection logic:

🔐 BRUTE_FORCE
Multiple failed login attempts in short time window
🚫 UNAUTHORIZED ACCESS
Repeated 403 errors or unauthorized patterns
🌊 FLOODING ATTACK
High request rate from single IP
⚠️ ABNORMAL BEHAVIOR
Heuristic anomaly detection based on activity scoring
📊 Alert Structure

Each alert contains:

IP Address / User
Alert Type
Severity (LOW / MEDIUM / HIGH)
Description
Risk Score
▶️ How to Run
Step 1

Launch JavaFX Application:

MainApp.java
Step 2

Load logs:

Click Load Logs
Or select log directory
Step 3

Analyze:

Click Analyze Activity
Step 4

View Results:

Alerts appear in right panel
Click View Alerts for details
💻 Demo Mode

A demo batch file is included:

run_demo.bat

It performs:

Compilation of core modules
Execution of analyzer tests
Console-based simulation
UI behavior demonstration
🧪 Demo Commands
javac -d out/classes src/main/java/...
java -cp out/classes com.example.dashboard.tools.TestRunner
java -cp out/classes com.example.dashboard.tools.AnalyzerMain resources/logs
java -cp out/classes com.example.dashboard.tools.UiSimulator resources/logs
📌 Sample Output
[ALERT] HIGH - ABNORMAL_BEHAVIOR (bot ip=198.51.100.1 score=100)
[ALERT] HIGH - FLOODING (bot ip=198.51.100.1 score=100)
[ALERT] MEDIUM - BRUTE_FORCE (user=alice ip=192.168.1.10 score=40)
[ALERT] LOW - UNAUTHORIZED (user=bob ip=10.0.0.5 score=20)

Report Summary:
Logs Processed: 75
Alerts Generated: 48
High: 39 | Medium: 5 | Low: 4
Blacklisted IPs: 1
🧪 Testing

Unit tests included:

LogAnalyzerTest
BRUTE_FORCE detection validation
UNAUTHORIZED detection validation
⚠️ Limitations

This system is:

❗ Rule-based (not AI/ML-based)
❗ Designed for academic/lab demonstration
❗ Not intended for production security environments
