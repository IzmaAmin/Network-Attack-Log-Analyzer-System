package com.example.dashboard.tools;

import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.service.LogAnalyzer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class UiSimulator {
    public static void main(String[] args) throws Exception {
        Path logs = args.length > 0 ? Paths.get(args[0]) : Paths.get("resources/logs");
        LogAnalyzer analyzer = new LogAnalyzer();
        List<com.example.dashboard.model.LogEntry> entries = analyzer.loadLogs(logs);
        LogAnalyzer.AnalysisResult res = analyzer.analyze(entries);

        System.out.println("=== Network Attack Log Analyzer (Console UI) ===");
        System.out.println("Loaded logs: " + entries.size());
        if (res.alerts.isEmpty()) {
            System.out.println("No alerts detected.");
            return;
        }

        for (int i = 0; i < res.alerts.size(); i++) {
            AlertEntry a = res.alerts.get(i);
            System.out.printf("%d) [%s] %s - %s (user=%s ip=%s score=%d)\n", i, a.getTimestamp(), a.getSeverity(), a.getType(), a.getUser(), a.getIp(), a.getScore());
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Enter alert index to see details (q to quit): ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("q") || line.isEmpty()) break;
            try {
                int idx = Integer.parseInt(line);
                if (idx < 0 || idx >= res.alerts.size()) { System.out.println("Invalid index"); continue; }
                AlertEntry a = res.alerts.get(idx);
                System.out.println("--- Details ---");
                System.out.println("Timestamp: " + a.getTimestamp());
                System.out.println("Type: " + a.getType());
                System.out.println("Severity: " + a.getSeverity());
                System.out.println("User: " + a.getUser());
                System.out.println("IP: " + a.getIp());
                System.out.println("Score: " + a.getScore());
                System.out.println("Description: " + a.getDescription());
                System.out.println("----------------");
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number or 'q'");
            }
        }
        System.out.println("Bye");
    }
}