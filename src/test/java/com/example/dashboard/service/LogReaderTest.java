package com.example.dashboard.service;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogReaderTest {

    @Test
    public void testParseLineAndRead() throws Exception {
        Path tmp = Files.createTempDirectory("dslogtest");
        Path file = tmp.resolve("user_activity.log");
        String line = "2025-12-18T09:55:00|user-monitor|INFO|user:testuser|ip:127.0.0.1|action:LOGIN|status:FAIL|details:badpass";
        Files.writeString(file, line + System.lineSeparator());

        LogReader reader = new LogReader(file);
        List<ParsedLogEntry> entries = reader.readAll();
        assertEquals(1, entries.size());
        ParsedLogEntry e = entries.get(0);
        assertEquals("testuser", e.getUser());
        assertEquals("127.0.0.1", e.getIp());
        assertEquals("LOGIN", e.getAction());
        assertEquals("FAIL", e.getStatus());
    }
}
