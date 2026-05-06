package com.example.dashboard.tools;

import com.example.dashboard.service.LogAnalyzer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        int failures = 0;
        System.out.println("Running TestRunner...");
        if (!testBruteForceDetection()) { System.out.println("testBruteForceDetection FAILED"); failures++; } else { System.out.println("testBruteForceDetection OK"); }
        if (!testUnauthorizedDetection()) { System.out.println("testUnauthorizedDetection FAILED"); failures++; } else { System.out.println("testUnauthorizedDetection OK"); }
        if (failures > 0) {
            System.out.println("Tests failed: " + failures);
            System.exit(1);
        } else {
            System.out.println("All tests passed.");
            System.exit(0);
        }
    }

    private static boolean testBruteForceDetection() throws Exception {
        Path tmp = Files.createTempDirectory("analyzertest");
        Path file = tmp.resolve("sample.log");
        StringBuilder sb = new StringBuilder();
        sb.append("2025-12-18T10:00:00|library|INFO|user:attacker|ip:10.0.0.1|action:LOGIN|status:FAIL|reason:bad").append(System.lineSeparator());
        sb.append("2025-12-18T10:01:00|library|INFO|user:attacker|ip:10.0.0.1|action:LOGIN|status:FAIL|reason:bad").append(System.lineSeparator());
        sb.append("2025-12-18T10:02:00|library|INFO|user:attacker|ip:10.0.0.1|action:LOGIN|status:FAIL|reason:bad").append(System.lineSeparator());
        sb.append("2025-12-18T10:03:00|library|INFO|user:attacker|ip:10.0.0.1|action:LOGIN|status:FAIL|reason:bad").append(System.lineSeparator());
        sb.append("2025-12-18T10:04:00|library|INFO|user:attacker|ip:10.0.0.1|action:LOGIN|status:FAIL|reason:bad").append(System.lineSeparator());
        Files.writeString(file, sb.toString());

        LogAnalyzer analyzer = new LogAnalyzer();
        List<com.example.dashboard.model.LogEntry> logs = analyzer.loadLogs(tmp);
        if (logs.size() != 5) return false;
        LogAnalyzer.AnalysisResult res = analyzer.analyze(logs);
        return res.alerts.stream().anyMatch(a -> "BRUTE_FORCE".equals(a.getType()));
    }

    private static boolean testUnauthorizedDetection() throws Exception {
        Path tmp = Files.createTempDirectory("analyzertest2");
        Path file = tmp.resolve("sample2.log");
        String line = "2025-12-18T11:00:00|library|WARN|user:bob|ip:10.0.0.2|action:ACCESS|status:403|details:unauthorized access attempt" + System.lineSeparator();
        Files.writeString(file, line);
        LogAnalyzer analyzer = new LogAnalyzer();
        List<com.example.dashboard.model.LogEntry> logs = analyzer.loadLogs(tmp);
        LogAnalyzer.AnalysisResult res = analyzer.analyze(logs);
        return res.alerts.stream().anyMatch(a -> "UNAUTHORIZED".equals(a.getType()));
    }
}
