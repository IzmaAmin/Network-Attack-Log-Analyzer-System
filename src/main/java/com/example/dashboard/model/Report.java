package com.example.dashboard.model;

import java.util.Map;

public class Report {
    private int totalLogs;
    private int totalAlerts;
    private Map<AlertEntry.Severity, Integer> alertsBySeverity;
    private int blacklistedCount;

    public Report(int totalLogs, int totalAlerts, Map<AlertEntry.Severity, Integer> alertsBySeverity, int blacklistedCount) {
        this.totalLogs = totalLogs;
        this.totalAlerts = totalAlerts;
        this.alertsBySeverity = alertsBySeverity;
        this.blacklistedCount = blacklistedCount;
    }

    public int getTotalLogs() { return totalLogs; }
    public int getTotalAlerts() { return totalAlerts; }
    public Map<AlertEntry.Severity, Integer> getAlertsBySeverity() { return alertsBySeverity; }
    public int getBlacklistedCount() { return blacklistedCount; }

    @Override
    public String toString() {
        return String.format("Report: logs=%d alerts=%d (H=%d M=%d L=%d) blacklisted=%d",
                totalLogs, totalAlerts,
                alertsBySeverity.getOrDefault(AlertEntry.Severity.HIGH,0),
                alertsBySeverity.getOrDefault(AlertEntry.Severity.MEDIUM,0),
                alertsBySeverity.getOrDefault(AlertEntry.Severity.LOW,0),
                blacklistedCount);
    }
}
