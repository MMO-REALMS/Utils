package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class Logger {

	public static LoggerInstance ACTIVE_INSTANCE;
	private static boolean installedPrintStream = false;

	static {
		ACTIVE_INSTANCE = LoggerInstance.DEFAULT;
	}

	public static LoggerInstance getInstance() {
		return ACTIVE_INSTANCE;
	}

	public static void setInstance(LoggerInstance loggerInstance) {
		Logger.ACTIVE_INSTANCE = loggerInstance;

		if(installedPrintStream){
			installPrintStream();
		}
	}

	public static void reset() {
		Logger.ACTIVE_INSTANCE = LoggerInstance.DEFAULT;
	}

	public static void debug(@Nullable Object object) {
		ACTIVE_INSTANCE.debug(object);
	}

	public static void debug(@Nullable Object object, ConsoleColor color) {
		ACTIVE_INSTANCE.debug(object, color);
	}

	public static void log(@Nullable Object object) {
		ACTIVE_INSTANCE.info(object);
	}

	public static void info(@Nullable Object object) {
		ACTIVE_INSTANCE.info(object);
	}

	public static void good(@Nullable Object object) {
		ACTIVE_INSTANCE.good(object);
	}

	public static void warn(@Nullable Object object) {
		ACTIVE_INSTANCE.warn(object);
	}

	public static void error(@Nullable Object object) {
		ACTIVE_INSTANCE.error(object);
	}

	public static void goodOrWarn(@Nullable Object object, boolean goodCheck) {
		if (goodCheck) {
			good(object);
		} else {
			warn(object);
		}
	}

	public static void printStackTrace() {
		try {
			throw new Exception();
		} catch (Exception e) {
			debug(StackTraceUtils.toString(e));
		}
	}

	public static void installPrintStream() {
		System.setOut(new PrintStream(new LoggerOutputStream(ACTIVE_INSTANCE::info, true)));
		System.setErr(new PrintStream(new LoggerOutputStream(ACTIVE_INSTANCE::error, false)));
		installedPrintStream = true;
	}

	public static void uninstallPrintStream() {
		installedPrintStream = false;
	}

}
