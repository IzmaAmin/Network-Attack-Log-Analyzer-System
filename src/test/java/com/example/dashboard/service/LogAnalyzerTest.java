package com.example.dashboard.service;

import com.example.dashboard.model.AlertEntry;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogAnalyzerTest {

    @Test
    public void testBruteForceDetection() throws Exception {
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
        assertEquals(5, logs.size());
        LogAnalyzer.AnalysisResult res = analyzer.analyze(logs);
        assertTrue(res.alerts.stream().anyMatch(a -> "BRUTE_FORCE".equals(a.getType())));
    }

    @Test
    public void testUnauthorizedDetection() throws Exception {
        Path tmp = Files.createTempDirectory("analyzertest2");
        Path file = tmp.resolve("sample2.log");
        String line = "2025-12-18T11:00:00|library|WARN|user:bob|ip:10.0.0.2|action:ACCESS|status:403|details:unauthorized access attempt" + System.lineSeparator();
        Files.writeString(file, line);
        LogAnalyzer analyzer = new LogAnalyzer();
        List<com.example.dashboard.model.LogEntry> logs = analyzer.loadLogs(tmp);
        LogAnalyzer.AnalysisResult res = analyzer.analyze(logs);
        assertTrue(res.alerts.stream().anyMatch(a -> "UNAUTHORIZED".equals(a.getType())));
    }
}