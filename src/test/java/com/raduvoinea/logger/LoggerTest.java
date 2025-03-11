package com.raduvoinea.logger;

import com.raduvoinea.logger.dto.TestLoggerHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoggerTest {

    @Test
    public void testDebugLogger() {
        Logger.reset();
        Logger.setLogLevel(Level.DEBUG);
        Logger.setLogHandler(new TestLoggerHandler());

        Logger.debug("testDebugLogger#debug");
        Logger.log("testDebugLogger#log");
        Logger.good("testDebugLogger#good");
        Logger.warn("testDebugLogger#warn");
        Logger.error("testDebugLogger#error");

        assertEquals(5, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
    }

    @Test
    public void testInfoLogger() {
        Logger.reset();
        Logger.setLogLevel(Level.INFO);
        Logger.setLogHandler(new TestLoggerHandler());

        Logger.debug("testInfoLogger#debug");
        Logger.log("testInfoLogger#log");
        Logger.good("testInfoLogger#good");
        Logger.warn("testInfoLogger#warn");
        Logger.error("testInfoLogger#error");

        assertEquals(4, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
    }

    @Test
    public void testWarnLogger() {
        Logger.reset();
        Logger.setLogLevel(Level.WARN);
        Logger.setLogHandler(new TestLoggerHandler());

        Logger.debug("testWarnLogger#debug");
        Logger.log("testWarnLogger#log");
        Logger.good("testWarnLogger#good");
        Logger.warn("testWarnLogger#warn");
        Logger.error("testWarnLogger#error");

        assertEquals(2, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
    }

    @Test
    public void testErrorLogger() {
        Logger.reset();
        Logger.setLogLevel(Level.ERROR);
        Logger.setLogHandler(new TestLoggerHandler());

        Logger.debug("testErrorLogger#debug");
        Logger.log("testErrorLogger#log");
        Logger.good("testErrorLogger#good");
        Logger.warn("testErrorLogger#warn");
        Logger.error("testErrorLogger#error");

        assertEquals(1, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
    }

    @Test
    public void testFormatClass() {
        Logger.reset();
        Logger.setLogHandler(new TestLoggerHandler());

        Logger.log("testErrorLogger#log");

        assertEquals(1, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
        assertEquals(ConsoleColor.RESET + "[LoggerTest] testErrorLogger#log" + ConsoleColor.RESET, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().getFirst());
    }

    @Test
    public void testFormatPackage() {
        Logger.reset();
        Logger.setLogHandler(new TestLoggerHandler());
        Logger.setPackageParser((packageName) -> "LoggerTestPackage");

        Logger.log("testErrorLogger#log");

        assertEquals(1, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().size());
        assertEquals(ConsoleColor.RESET + "[LoggerTestPackage] testErrorLogger#log" + ConsoleColor.RESET, ((TestLoggerHandler) Logger.getLogHandler()).getBuffer().getFirst());
    }

}
