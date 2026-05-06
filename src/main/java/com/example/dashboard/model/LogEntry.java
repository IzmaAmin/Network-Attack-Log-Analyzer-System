package com.example.dashboard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final LocalDateTime timestamp;
    private final String sourceApp;
    private final String level;
    private final String user;
    private final String ip;
    private final String action;
    private final String status;
    private final String details;
    private final String raw;

    public LogEntry(LocalDateTime timestamp,
                    String sourceApp,
                    String level,
                    String user,
                    String ip,
                    String action,
                    String status,
                    String details,
                    String raw) {
        this.timestamp = timestamp;
        this.sourceApp = sourceApp;
        this.level = level;
        this.user = user;
        this.ip = ip;
        this.action = action;
        this.status = status;
        this.details = details;
        this.raw = raw;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSourceApp() { return sourceApp; }
    public String getLevel() { return level; }
    public String getUser() { return user; }
    public String getIp() { return ip; }
    public String getAction() { return action; }
    public String getStatus() { return status; }
    public String getDetails() { return details; }
    public String getRaw() { return raw; }

    public String getTimestampString() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s %s %s %s",
                getTimestampString(), sourceApp, user, ip, action, status);
    }
}
