package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.logger.annotations.LogAsJson;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

@Getter
@Setter
public abstract class LoggerInstance {

	public static final LoggerInstance DEFAULT;
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	private Level logLevel;
	private boolean printSourceClass;

	public LoggerInstance() {
		this(Level.TRACE, true);
	}

	public LoggerInstance(Level logLevel, boolean printSourceClass) {
		this.logLevel = logLevel;
		this.printSourceClass = printSourceClass;
	}

	static {
		DEFAULT = new LoggerInstance() {
			private final PrintStream outPrintStream = System.out;
			private final PrintStream errPrintStream = System.err;

			@Override
			protected void handleInfo(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected void handleError(@NotNull String log) {
				this.errPrintStream.println(log);
				this.errPrintStream.flush();
			}

			@Override
			protected void handleWarn(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected void handleDebug(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected @Nullable String parsePackage(String packageName) {
				return null;
			}
		};
	}

	public void debug(@Nullable Object object) {
		this.debug(object, ConsoleColor.BRIGHT_BLACK);
	}

	public void debug(@Nullable Object object, ConsoleColor color) {
		this.log(Level.DEBUG, object, color, this::handleDebug);
	}

	public void info(@Nullable Object object) {
		this.log(Level.INFO, object, ConsoleColor.RESET, this::handleInfo);
	}

	public void good(@Nullable Object object) {
		this.log(Level.INFO, object, ConsoleColor.DARK_GREEN, this::handleInfo);
	}

	public void warn(@Nullable Object object) {
		this.log(Level.WARN, object, ConsoleColor.DARK_YELLOW, this::handleWarn);
	}

	public void error(@Nullable Object object) {
		this.log(Level.ERROR, object, ConsoleColor.DARK_RED, this::handleError);
	}

	private void log(@NotNull Level level, @Nullable Object object, @NotNull ConsoleColor color, ArgLambda<String> logger) {
		if (level.getLevel() < logLevel.getLevel()) {
			return;
		}

		String log = formatObject(object);

		StringBuilder finalLog = new StringBuilder(log.length() + 32);
		finalLog.append(color);

		if (printSourceClass) {
			Class<?> caller = getCallerClass();
			String id = this.parsePackage(caller.getPackageName());
			if (id == null || id.isEmpty()) {
				id = caller.getSimpleName();
			}
			finalLog.append('[').append(id).append("] ");
		}

		// Inline multi-line coloring without split/join
		for (int i = 0; i < log.length(); i++) {
			char c = log.charAt(i);
			if (c == '\n') {
				finalLog.append('\n').append(color);
			} else {
				finalLog.append(c);
			}
		}
		finalLog.append(ConsoleColor.RESET);

		String processed = finalProcessor(finalLog.toString());

		if (processed == null) {
			return;
		}

		logger.run(processed);
	}

	private static String formatObject(@Nullable Object object) {
		if (object == null) {
			return "null";
		}

		if (object.getClass().isAnnotationPresent(LogAsJson.class)) {
			return Logger.getGsonHolder().value().toJson(object);
		}

		return switch (object) {
			case GenericMessageBuilder<?> messageBuilder -> messageBuilder.toString();
			case Throwable throwable -> StackTraceUtils.toString(throwable);
			case StackTraceElement[] stackTraceElements -> StackTraceUtils.toString(stackTraceElements);
			default -> object.toString();
		};
	}

	protected abstract void handleInfo(@NotNull String log);

	protected abstract void handleError(@NotNull String log);

	protected abstract void handleWarn(@NotNull String log);

	protected abstract void handleDebug(@NotNull String log);

	protected @Nullable String finalProcessor(String log) {
		return log;
	}

	protected abstract @Nullable String parsePackage(String packageName);

	private static @NotNull Class<?> getCallerClass() {
		Class<?> clazz = STACK_WALKER.walk(stack -> stack.map(StackWalker.StackFrame::getDeclaringClass)
				.filter(c -> !Logger.class.isAssignableFrom(c) && !LoggerInstance.class.isAssignableFrom(c))
				.findFirst()
		).orElse(null);

		if (clazz == null) {
			System.out.println("<!> Failed to get caller class <!>");
			clazz = Logger.class;
		}

		return clazz;
	}

}
