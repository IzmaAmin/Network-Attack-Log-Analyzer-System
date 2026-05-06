package com.example.dashboard.controller;


import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.model.LogEntry;
import com.example.dashboard.model.Report;
import com.example.dashboard.service.LogAnalyzer;
import com.example.dashboard.service.LogAnalyzer.AnalysisResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class MainController {
    private final LogAnalyzer analyzer = new LogAnalyzer();
    private final ObservableList<LogEntry> logs = FXCollections.observableArrayList();
    private final ObservableList<AlertEntry> alerts = FXCollections.observableArrayList();
    private final ObservableList<String> blacklist = FXCollections.observableArrayList();
    private Report lastReport = null;

    // default logs folder
    private Path logsDir = Paths.get("resources/logs");

    public ObservableList<LogEntry> getLogs() { return logs; }
    public ObservableList<AlertEntry> getAlerts() { return alerts; }
    public ObservableList<String> getBlacklist() { return blacklist; }
    public Report getLastReport() { return lastReport; }
    public void setLogsDir(Path p) { this.logsDir = p; }
    public Path getLogsDir() { return logsDir; }

    public void loadLogs() throws IOException {
        List<LogEntry> loaded = analyzer.loadLogs(logsDir);
        logs.setAll(loaded);
    }

    public void analyze() {
        AnalysisResult res = analyzer.analyze(logs);
        alerts.setAll(res.alerts);
        blacklist.setAll(res.blacklist);
        lastReport = res.report;
    }

    public Report generateReport() {
        return lastReport;
    }
}
