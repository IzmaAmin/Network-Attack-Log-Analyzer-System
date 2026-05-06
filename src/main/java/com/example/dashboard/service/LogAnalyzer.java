package com.example.dashboard.service;

import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.model.LogEntry;
import com.example.dashboard.model.Report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LogAnalyzer {
    private final Map<String, Integer> riskByIp = new HashMap<>();
    private final Map<String, Integer> riskByUser = new HashMap<>();
    private final Set<String> blacklist = new HashSet<>();
    private final PriorityQueue<AlertEntry> alertQueue = new PriorityQueue<>();

    // detection parameters (demo values)
    private final Duration bruteWindow = Duration.ofMinutes(5);
    private final int bruteThreshold = 5;

    private final Duration floodWindow = Duration.ofMinutes(1);
    private final int floodThreshold = 50; // actions per minute

    public List<LogEntry> loadLogs(Path logDir) throws IOException {
        List<LogEntry> out = new ArrayList<>();
        File dir = logDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return out;
        File[] files = dir.listFiles((d, n) -> n.endsWith(".log"));
        if (files == null) return out;
        for (File f : files) {
            // Special-case the monitoring client format: use LogReader to parse and normalize
            if ("user_activity.log".equalsIgnoreCase(f.getName())) {
                LogReader lr = new LogReader(f.toPath());
                List<ParsedLogEntry> parsed = lr.readAll();
                for (ParsedLogEntry p : parsed) {
                    String user = p.getUser() == null ? "" : p.getUser().trim();
                    String ip = p.getIp() == null ? "" : p.getIp().trim();
                    String action = p.getAction() == null ? "" : p.getAction().trim().toUpperCase();
                    String status = p.getStatus() == null ? "" : p.getStatus().trim().toUpperCase();
                    String details = p.getDetails() == null ? "" : p.getDetails();
                    LogEntry e = new LogEntry(p.getTimestamp(), "user-monitor", p.getLevel(), user, ip, action, status, details, "");
                    out.add(e);
                }
                continue;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    LogEntry e = parseLine(line);
                    if (e != null) out.add(e);
                }
            }
        }
        // sort by timestamp
        out.sort(Comparator.comparing(LogEntry::getTimestamp));
        return out;
    }

    private LogEntry parseLine(String line) {
        try {
            if (line == null || line.trim().isEmpty()) return null;
            String[] parts = line.split("\\|");
            String tsRaw = parts.length > 0 ? parts[0].trim() : "";
            LocalDateTime ts;
            try {
                ts = LocalDateTime.parse(tsRaw);
            } catch (Exception ex) {
                try {
                    ts = LocalDateTime.parse(tsRaw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception ex2) {
                    return null;
                }
            }
            String app = parts.length > 1 ? parts[1].trim() : "";
            String level = parts.length > 2 ? parts[2].trim() : "";
            String user = extractValue(parts, "user", "").trim();
            String ip = extractValue(parts, "ip", "").trim();
            String action = extractValue(parts, "action", "").trim().toUpperCase();
            String status = extractValue(parts, "status", "").trim().toUpperCase();
            String details = parts.length > 3 ? Arrays.stream(parts).skip(3).collect(Collectors.joining(" | ")) : "";
            return new LogEntry(ts, app, level, user, ip, action, status, details, line);
        } catch (Exception ex) {
            return null;
        }
    }

    private String extractValue(String[] parts, String key, String defaultVal) {
        for (String p : parts) {
            if (p.startsWith(key + ":")) return p.substring((key + ":").length());
        }
        return defaultVal;
    }

    public AnalysisResult analyze(List<LogEntry> logs) {
        // reset
        riskByIp.clear();
        riskByUser.clear();
        blacklist.clear();
        alertQueue.clear();

        Map<String, Deque<LocalDateTime>> failedLoginsByUser = new HashMap<>();
        Map<String, Deque<LocalDateTime>> activityByIp = new HashMap<>();

        for (LogEntry e : logs) {
            LocalDateTime t = e.getTimestamp();
            String ip = e.getIp();
            String user = e.getUser();
            // track activity per IP for flood detection
            activityByIp.computeIfAbsent(ip, k -> new ArrayDeque<>()).addLast(t);
            Deque<LocalDateTime> dq = activityByIp.get(ip);
            // evict older than floodWindow
            while (!dq.isEmpty() && Duration.between(dq.peekFirst(), t).compareTo(floodWindow) > 0) dq.removeFirst();
            if (dq.size() >= floodThreshold) {
                int incr = 30;
                bumpRisk(ip, user, incr);
                pushAlert(t, "FLOODING", ip, user, "High activity rate: " + dq.size() + " events in last " + floodWindow.toMinutes() + "m", computeSeverityForRisk(ip, user));
            }

            // brute-force detection: failed logins
            if ("LOGIN".equalsIgnoreCase(e.getAction()) && "FAIL".equalsIgnoreCase(e.getStatus())) {
                failedLoginsByUser.computeIfAbsent(user, k -> new ArrayDeque<>()).addLast(t);
                Deque<LocalDateTime> fd = failedLoginsByUser.get(user);
                while (!fd.isEmpty() && Duration.between(fd.peekFirst(), t).compareTo(bruteWindow) > 0) fd.removeFirst();
                if (fd.size() >= bruteThreshold) {
                    int incr = 40;
                    bumpRisk(ip, user, incr);
                    pushAlert(t, "BRUTE_FORCE", ip, user, "Failed login attempts: " + fd.size() + " in last " + bruteWindow.toMinutes() + "m", computeSeverityForRisk(ip, user));
                }
            }

            // repeated unauthorized access
            if (e.getDetails().toLowerCase().contains("unauthorized") || e.getStatus().equalsIgnoreCase("403")) {
                int incr = 20;
                bumpRisk(ip, user, incr);
                pushAlert(t, "UNAUTHORIZED", ip, user, "Repeated unauthorized access detected", computeSeverityForRisk(ip, user));
            }

            // abnormal user behaviour - simplistic heuristic: many actions in short time by same user
            Deque<LocalDateTime> userActivity = activityByIp.getOrDefault(ip, new ArrayDeque<>());
            if (userActivity.size() > floodThreshold / 2) {
                int incr = 10;
                bumpRisk(ip, user, incr);
                pushAlert(t, "ABNORMAL_BEHAVIOR", ip, user, "Unusually high action diversity/rate", computeSeverityForRisk(ip, user));
            }

            // apply blacklist rule
            int risk = Math.max(riskByIp.getOrDefault(ip, 0), riskByUser.getOrDefault(user, 0));
            if (risk >= 80) {
                blacklist.add(ip);
            }
        }

        // build report
        Map<AlertEntry.Severity, Integer> bySeverity = new EnumMap<>(AlertEntry.Severity.class);
        for (AlertEntry.Severity s : AlertEntry.Severity.values()) bySeverity.put(s, 0);
        List<AlertEntry> alerts = new ArrayList<>();
        while (!alertQueue.isEmpty()) {
            AlertEntry a = alertQueue.poll();
            alerts.add(a);
            bySeverity.put(a.getSeverity(), bySeverity.getOrDefault(a.getSeverity(),0)+1);
        }

        Report report = new Report(logs.size(), alerts.size(), bySeverity, blacklist.size());
        return new AnalysisResult(alerts, new HashSet<>(blacklist), report, new HashMap<>(riskByIp), new HashMap<>(riskByUser));
    }

    private void bumpRisk(String ip, String user, int amount) {
        if (ip != null && !ip.isEmpty()) riskByIp.put(ip, Math.min(100, riskByIp.getOrDefault(ip, 0) + amount));
        if (user != null && !user.isEmpty()) riskByUser.put(user, Math.min(100, riskByUser.getOrDefault(user, 0) + amount));
    }

    private AlertEntry.Severity computeSeverityForRisk(String ip, String user) {
        int r = Math.max(riskByIp.getOrDefault(ip, 0), riskByUser.getOrDefault(user, 0));
        if (r >= 80) return AlertEntry.Severity.HIGH;
        if (r >= 40) return AlertEntry.Severity.MEDIUM;
        return AlertEntry.Severity.LOW;
    }

    private void pushAlert(LocalDateTime t, String type, String ip, String user, String description, AlertEntry.Severity severity) {
        int score = Math.max(riskByIp.getOrDefault(ip, 0), riskByUser.getOrDefault(user,0));
        alertQueue.add(new AlertEntry(t, severity, type, ip, user, description, score));
    }

    public static class AnalysisResult {
        public final List<AlertEntry> alerts;
        public final Set<String> blacklist;
        public final Report report;
        public final Map<String,Integer> riskByIp;
        public final Map<String,Integer> riskByUser;

        public AnalysisResult(List<AlertEntry> alerts, Set<String> blacklist, Report report, Map<String, Integer> riskByIp, Map<String, Integer> riskByUser) {
            this.alerts = alerts;
            this.blacklist = blacklist;
            this.report = report;
            this.riskByIp = riskByIp;
            this.riskByUser = riskByUser;
        }
    }
}
