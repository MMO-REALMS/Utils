package com.raduvoinea.utils.logger.utils;

public class StackTraceUtils {

	public static String toString(Throwable throwable) {
		StringBuilder builder = new StringBuilder(512);
		appendThrowable(builder, throwable);
		return builder.toString();
	}

	private static void appendThrowable(StringBuilder builder, Throwable throwable) {
		builder.append(throwable).append('\n');
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("\tat ").append(element).append('\n');
		}
		Throwable cause = throwable.getCause();
		if (cause != null) {
			builder.append("Caused by: ");
			appendThrowable(builder, cause);
		}
	}

	public static String toString(StackTraceElement[] stackTrace) {
		StringBuilder builder = new StringBuilder(stackTrace.length * 48);

		for (StackTraceElement element : stackTrace) {
			builder.append(element.toString()).append('\n');
		}

		return builder.toString();
	}
}
