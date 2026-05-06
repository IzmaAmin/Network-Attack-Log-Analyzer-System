package com.example.dashboard.tools;

import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.model.LogEntry;
import com.example.dashboard.service.LogAnalyzer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class IntegrationCheckMain {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("integrationlogs");
        Path ua = tmp.resolve("user_activity.log");
        Path lib = tmp.resolve("library_activity.log");

        StringBuilder s1 = new StringBuilder();
        s1.append("2025-12-18T09:55:00|user-monitor|INFO|user:mallory|ip:203.0.113.10|action:post_comment|status:OK|content:spam1\n");
        s1.append("2025-12-18T09:55:03|user-monitor|INFO|user:mallory|ip:203.0.113.10|action:post_comment|status:OK|content:spam2\n");
        Files.writeString(ua, s1.toString());

        StringBuilder s2 = new StringBuilder();
        s2.append("2025-12-18T09:52:10|library|INFO|user:alice|ip:192.168.1.10|action:LOGIN|status:FAIL|reason:wrong_password\n");
        s2.append("2025-12-18T09:52:15|library|INFO|user:alice|ip:192.168.1.10|action:LOGIN|status:FAIL|reason:wrong_password\n");
        s2.append("2025-12-18T09:52:25|library|INFO|user:alice|ip:192.168.1.10|action:LOGIN|status:FAIL|reason:wrong_password\n");
        s2.append("2025-12-18T09:52:35|library|INFO|user:alice|ip:192.168.1.10|action:LOGIN|status:FAIL|reason:wrong_password\n");
        s2.append("2025-12-18T09:52:45|library|INFO|user:alice|ip:192.168.1.10|action:LOGIN|status:FAIL|reason:wrong_password\n");
        Files.writeString(lib, s2.toString());

        LogAnalyzer analyzer = new LogAnalyzer();
        List<LogEntry> logs = analyzer.loadLogs(tmp);
        System.out.println("Loaded " + logs.size() + " entries from " + tmp.toAbsolutePath());
        for (LogEntry l : logs) System.out.println(l);

        LogAnalyzer.AnalysisResult res = analyzer.analyze(logs);
        System.out.println("Alerts:");
        for (AlertEntry a : res.alerts) System.out.println(a);
    }
}
