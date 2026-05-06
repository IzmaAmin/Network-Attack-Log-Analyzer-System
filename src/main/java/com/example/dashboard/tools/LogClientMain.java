package com.example.dashboard.tools;

import com.example.dashboard.service.LogReader;
import com.example.dashboard.service.ParsedLogEntry;

import java.nio.file.Path;
import java.util.List;

public class LogClientMain {
    public static void main(String[] args) {
        Path file = LogReader.resolveLogFile(() -> System.getenv("USER_ACTIVITY_LOG_DIR"), "resources/logs");
        LogReader reader = new LogReader(file);
        List<ParsedLogEntry> entries = reader.readAll();
        System.out.println("Found " + entries.size() + " entries in " + file.toAbsolutePath());
        for (ParsedLogEntry e : entries) {
            System.out.println(e);
        }
    }
}
