package com.example.dashboard.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Lightweight parser for the external user_activity.log produced by the monitoring client.
 * Resilient and uses plain Java SE only.
 */
public class LogReader {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Path file;

    public LogReader(Path file) { this.file = file; }

    public static Path resolveLogFile(Supplier<String> envSupplier, String fallback) {
        String env = envSupplier == null ? null : envSupplier.get();
        Path dir = env != null && !env.isEmpty() ? Paths.get(env) : Paths.get(System.getProperty("monitoring.log.dir", fallback));
        return dir.resolve("user_activity.log");
    }

    public List<ParsedLogEntry> readAll() {
        List<ParsedLogEntry> out = new ArrayList<>();
        if (file == null) return out;
        try {
            if (!Files.exists(file)) return out;
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                try {
                    ParsedLogEntry e = parseLine(line);
                    if (e != null) out.add(e);
                } catch (Exception ex) {
                    // ignore malformed lines
                }
            }
        } catch (IOException ex) {
            // best-effort: return what we have or empty list
        }
        return out;
    }

    private ParsedLogEntry parseLine(String line) {
        // Expected format: TIMESTAMP|user-monitor|LEVEL|user:USERNAME|ip:IPADDR|action:NAME|status:OK|details:...
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", 8);
        if (parts.length < 7) return null;
        String ts = parts[0];
        LocalDateTime time = LocalDateTime.parse(ts, F);
        String level = parts[2];
        String user = extractValue(parts[3], "user");
        String ip = extractValue(parts[4], "ip");
        String action = extractValue(parts[5], "action");
        String status = extractValue(parts[6], "status");
        String details = parts.length >= 8 ? parts[7] : "";
        return new ParsedLogEntry(time, level, user, ip, action, status, details);
    }

    private String extractValue(String segment, String prefix) {
        if (segment == null) return "";
        int idx = segment.indexOf(':');
        if (idx < 0) return segment;
        return segment.substring(idx + 1);
    }
}
