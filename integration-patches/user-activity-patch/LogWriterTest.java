package com.monitoring.util;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class LogWriterTest {
    @Test
    void writesExpectedLine() throws Exception {
        Path tmp = Files.createTempDirectory("logtest");
        Path file = LogWriter.resolveLogFile(() -> tmp.toString(), tmp.toString());
        LogWriter lw = new LogWriter(file);
        lw.log("INFO", "testUser", "127.0.0.1", "LOGIN", "FAIL", "reason:test");

        String line = Files.readAllLines(file).get(0);
        assertTrue(line.contains("|user-monitor|INFO|user:testUser|ip:127.0.0.1|action:LOGIN|status:FAIL|"));
        String ts = line.split("\\|", 2)[0];
        LocalDateTime.parse(ts); // parseable timestamp
    }
}
