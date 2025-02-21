package com.raduvoinea.logger;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.Level;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoggerTest {

    @SneakyThrows
    @Test
    public void testDebugLogger() {
        List<String> printed = new ArrayList<>();
        Logger.setLogLevel(Level.DEBUG);
        Logger.setLogHandler(printed::add);

        Logger.debug("testDebugLogger#debug");
        Logger.log("testDebugLogger#log");
        Logger.good("testDebugLogger#good");
        Logger.warn("testDebugLogger#warn");
        Logger.error("testDebugLogger#error");

        assertEquals(5, printed.size());
    }

    @SneakyThrows
    @Test
    public void testInfoLogger() {
        List<String> printed = new ArrayList<>();
        Logger.setLogLevel(Level.INFO);
        Logger.setLogHandler(printed::add);

        Logger.debug("testInfoLogger#debug");
        Logger.log("testInfoLogger#log");
        Logger.good("testInfoLogger#good");
        Logger.warn("testInfoLogger#warn");
        Logger.error("testInfoLogger#error");

        assertEquals(4, printed.size());
    }

    @SneakyThrows
    @Test
    public void testWarnLogger() {
        List<String> printed = new ArrayList<>();
        Logger.setLogLevel(Level.WARN);
        Logger.setLogHandler(printed::add);

        Logger.debug("testWarnLogger#debug");
        Logger.log("testWarnLogger#log");
        Logger.good("testWarnLogger#good");
        Logger.warn("testWarnLogger#warn");
        Logger.error("testWarnLogger#error");

        assertEquals(2, printed.size());
    }

    @SneakyThrows
    @Test
    public void testErrorLogger() {
        List<String> printed = new ArrayList<>();
        Logger.setLogLevel(Level.ERROR);
        Logger.setLogHandler(printed::add);

        Logger.debug("testErrorLogger#debug");
        Logger.log("testErrorLogger#log");
        Logger.good("testErrorLogger#good");
        Logger.warn("testErrorLogger#warn");
        Logger.error("testErrorLogger#error");

        assertEquals(1, printed.size());
    }


}
