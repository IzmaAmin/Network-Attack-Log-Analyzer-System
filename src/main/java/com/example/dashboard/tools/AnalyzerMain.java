package com.example.dashboard.tools;

import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.service.LogAnalyzer;
import com.example.dashboard.service.LogAnalyzer.AnalysisResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AnalyzerMain {
    public static void main(String[] args) throws Exception {
        Path logs = args.length > 0 ? Paths.get(args[0]) : Paths.get("resources/logs");
        LogAnalyzer analyzer = new LogAnalyzer();
        List<com.example.dashboard.model.LogEntry> entries = analyzer.loadLogs(logs);
        AnalysisResult res = analyzer.analyze(entries);
        System.out.println("Alerts:");
        for (AlertEntry a : res.alerts) System.out.println(a);
        System.out.println("Report: " + res.report);
    }
}