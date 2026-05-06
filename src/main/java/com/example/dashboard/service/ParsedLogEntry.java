package com.example.dashboard.service;

import java.time.LocalDateTime;

public class ParsedLogEntry {
    private final LocalDateTime timestamp;
    private final String level;
    private final String user;
    private final String ip;
    private final String action;
    private final String status;
    private final String details;

    public ParsedLogEntry(LocalDateTime timestamp, String level, String user, String ip, String action, String status, String details) {
        this.timestamp = timestamp;
        this.level = level;
        this.user = user;
        this.ip = ip;
        this.action = action;
        this.status = status;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getUser() { return user; }
    public String getIp() { return ip; }
    public String getAction() { return action; }
    public String getStatus() { return status; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s | %s", timestamp, level, user, ip, action, status);
    }
}
