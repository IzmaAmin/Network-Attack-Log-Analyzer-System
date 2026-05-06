package com.example.dashboard.model;

import java.time.LocalDateTime;

public class AlertEntry implements Comparable<AlertEntry> {
    public enum Severity {LOW, MEDIUM, HIGH}

    private final LocalDateTime timestamp;
    private final Severity severity;
    private final String type;
    private final String ip;
    private final String user;
    private final String description;
    private final int score;

    public AlertEntry(LocalDateTime timestamp, Severity severity, String type, String ip, String user, String description, int score) {
        this.timestamp = timestamp;
        this.severity = severity;
        this.type = type;
        this.ip = ip;
        this.user = user;
        this.description = description;
        this.score = score;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Severity getSeverity() { return severity; }
    public String getType() { return type; }
    public String getIp() { return ip; }
    public String getUser() { return user; }
    public String getDescription() { return description; }
    public int getScore() { return score; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (user=%s ip=%s score=%d)",
                timestamp.toString(), severity, type, user, ip, score);
    }

    @Override
    public int compareTo(AlertEntry other) {
        // Higher severity first, then higher score
        int severityCmp = other.severity.ordinal() - this.severity.ordinal();
        if (severityCmp != 0) return severityCmp;
        return Integer.compare(other.score, this.score);
    }
}
