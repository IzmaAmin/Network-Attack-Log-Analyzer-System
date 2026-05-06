package com.monitoring.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Small demo to verify LogWriter writes expected sample events
 */
public class MonitoringIntegrationDemo {
    public static void main(String[] args) throws Exception {
        Path out = LogWriter.resolveLogFile(() -> System.getenv("USER_ACTIVITY_LOG_DIR"), "resources/logs");
        LogWriter lw = new LogWriter(out);
        lw.log("INFO", "demoUser", "127.0.0.1", "LOGIN", "OK", "demo login");
        lw.log("INFO", "demoUser", "127.0.0.1", "POST_COMMENT", "OK", "length:12");
        System.out.println("Wrote demo lines to: " + out.toAbsolutePath());
    }
}
