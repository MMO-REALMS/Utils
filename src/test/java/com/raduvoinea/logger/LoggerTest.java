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
		Logger.setInstance(new TestLoggerHandler());

		Logger.debug("testDebugLogger#debug");
		Logger.log("testDebugLogger#log");
		Logger.good("testDebugLogger#good");
		Logger.warn("testDebugLogger#warn");
		Logger.error("testDebugLogger#error");

		assertEquals(5, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
	}

	@Test
	public void testInfoLogger() {
		Logger.reset();
		Logger.setInstance(new TestLoggerHandler());
		Logger.setLogLevel(Level.INFO);

		Logger.debug("testInfoLogger#debug");
		Logger.log("testInfoLogger#log");
		Logger.good("testInfoLogger#good");
		Logger.warn("testInfoLogger#warn");
		Logger.error("testInfoLogger#error");

		assertEquals(4, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
	}

	@Test
	public void testWarnLogger() {
		Logger.reset();
		Logger.setInstance(new TestLoggerHandler());
		Logger.setLogLevel(Level.WARN);

		Logger.debug("testWarnLogger#debug");
		Logger.log("testWarnLogger#log");
		Logger.good("testWarnLogger#good");
		Logger.warn("testWarnLogger#warn");
		Logger.error("testWarnLogger#error");

		assertEquals(2, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
	}

	@Test
	public void testErrorLogger() {
		Logger.reset();
		Logger.setInstance(new TestLoggerHandler());
		Logger.setLogLevel(Level.ERROR);

		Logger.debug("testErrorLogger#debug");
		Logger.log("testErrorLogger#log");
		Logger.good("testErrorLogger#good");
		Logger.warn("testErrorLogger#warn");
		Logger.error("testErrorLogger#error");

		assertEquals(1, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
	}

	@Test
	public void testFormatClass() {
		Logger.reset();
		Logger.setInstance(new TestLoggerHandler());

		Logger.log("testErrorLogger#log");

		assertEquals(1, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
		assertEquals(ConsoleColor.RESET + "[LoggerTest] testErrorLogger#log" + ConsoleColor.RESET, ((TestLoggerHandler) Logger.getInstance()).getBuffer().getFirst());
	}

	@Test
	public void testFormatPackage() {
		Logger.reset();
		Logger.setInstance(new TestLoggerHandler(true));

		Logger.log("testErrorLogger#log");

		assertEquals(1, ((TestLoggerHandler) Logger.getInstance()).getBuffer().size());
		assertEquals(ConsoleColor.RESET + "[LoggerTestPackage] testErrorLogger#log" + ConsoleColor.RESET, ((TestLoggerHandler) Logger.getInstance()).getBuffer().getFirst());
	}

}
