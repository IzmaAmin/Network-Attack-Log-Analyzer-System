package com.monitoring.util;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * Minimal, safe file-based logger for User Activity Monitoring System.
 * Usage: LogWriter lw = new LogWriter(LogWriter.resolveLogFile(() -> System.getenv("USER_ACTIVITY_LOG_DIR"), "resources/logs"));
 * Call: lw.log("INFO", user, ip, "POST_COMMENT", "OK", "length:123");
 */
public class LogWriter {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Path file;

    public LogWriter(Path file) {
        this.file = file;
    }

    public static Path resolveLogFile(Supplier<String> envSupplier, String fallback) {
        String env = envSupplier.get();
        Path dir = (env != null && !env.isEmpty()) ? Paths.get(env) : Paths.get(System.getProperty("monitoring.log.dir", fallback));
        try { Files.createDirectories(dir); } catch (IOException ignored) {}
        return dir.resolve("user_activity.log");
    }

    public synchronized void log(String level, String user, String ip, String action, String status, String details) {
        try {
            String safeDetails = (details == null) ? "" : (details.length() > 512 ? details.substring(0,512) + "...(truncated)" : details);
            String line = String.format("%s|user-monitor|%s|user:%s|ip:%s|action:%s|status:%s|%s",
                    LocalDateTime.now().format(F),
                    (level == null ? "INFO" : level),
                    (user == null ? "" : user),
                    (ip == null ? "" : ip),
                    (action == null ? "" : action),
                    (status == null ? "" : status),
                    safeDetails);
            Files.writeString(file, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ex) {
            // Swallow to avoid affecting app flow
        }
    }
}
