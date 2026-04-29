package com.raduvoinea.utils.logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class Logger {

	private static LoggerInstance activeInstance;

	private static PrintStream originalPrintStreamOut;
	private static PrintStream originalPrintStreamErr;

	private static boolean installedPrintStream = false;
	@Getter
	private static Holder<Gson> gsonHolder;

	static {
		activeInstance = LoggerInstance.DEFAULT;

		gsonHolder = Holder.of(
				new GsonBuilder()
						.disableHtmlEscaping()
						.create()
		);
	}

	public static void setGsonHolder(Holder<Gson> gsonHolder) {
		Logger.gsonHolder = gsonHolder;
	}

	public static LoggerInstance getInstance() {
		return activeInstance;
	}

	public static void setInstance(LoggerInstance loggerInstance) {
		Logger.activeInstance = loggerInstance;

		if (installedPrintStream) {
			installPrintStream();
		}
	}

	public static void reset() {
		Logger.activeInstance = LoggerInstance.DEFAULT;
	}

	public static void debug(@Nullable Object object) {
		activeInstance.debug(object);
	}

	public static void debug(@Nullable Object object, ConsoleColor color) {
		activeInstance.debug(object, color);
	}

	public static void log(@Nullable Object object) {
		activeInstance.info(object);
	}

	public static void info(@Nullable Object object) {
		activeInstance.info(object);
	}

	public static void good(@Nullable Object object) {
		activeInstance.good(object);
	}

	public static void warn(@Nullable Object object) {
		activeInstance.warn(object);
	}

	public static void error(@Nullable Object object) {
		activeInstance.error(object);
	}

	public static KvLog kv(@NotNull String type) {
		return new KvLog(type);
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
		if (originalPrintStreamOut == null) {
			originalPrintStreamOut = System.out;
		}
		if (originalPrintStreamErr == null) {
			originalPrintStreamErr = System.err;
		}

		System.setOut(new PrintStream(new LoggerOutputStream(activeInstance::info, true)));
		System.setErr(new PrintStream(new LoggerOutputStream(activeInstance::error, false)));
		installedPrintStream = true;
	}

	public static void uninstallPrintStream() {
		if (originalPrintStreamOut != null) {
			System.setOut(originalPrintStreamOut);
		}
		if (originalPrintStreamErr != null) {
			System.setErr(originalPrintStreamErr);
		}
		installedPrintStream = false;
	}

}
