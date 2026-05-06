package Library.util;

import java.nio.file.Path;

/**
 * Demo to verify existing LogWriter writes sample events for the dashboard
 */
public class LibraryLogIntegrationDemo {
    public static void main(String[] args) throws Exception {
        Path out = LogWriter.resolveLogFile(() -> System.getenv("LIBRARY_LOG_DIR"), "resources/logs");
        LogWriter lw = new LogWriter(out);
        lw.log("INFO", "demoUser", "192.168.1.100", "LOGIN", "FAIL", "demo failed login");
        lw.log("INFO", "demoUser", "192.168.1.100", "CHECKOUT", "OK", "bookId:123");
        System.out.println("Wrote demo lines to: " + out.toAbsolutePath());
    }
}
