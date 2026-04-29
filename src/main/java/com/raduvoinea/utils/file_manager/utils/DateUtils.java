package com.raduvoinea.utils.file_manager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class DateUtils {

	private static final ConcurrentHashMap<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>();

	public static @NotNull String getDate(String format) {
		DateTimeFormatter formatter = FORMATTERS.computeIfAbsent(format, DateTimeFormatter::ofPattern);
		return formatter.format(java.time.ZonedDateTime.now());
	}

	@SuppressWarnings("unused")
	public static @NotNull String getDateAndTime() {
		return getDate("HH:mm:ss dd-MM-yyyy");
	}

	@SuppressWarnings("unused")
	public static @NotNull String getDateOnly() {
		return getDate("dd-MM-yyyy");
	}

	@SuppressWarnings("unused")
	public static @NotNull String getTimeOnly() {
		return getDate("HH:mm:ss");
	}

	@SuppressWarnings("unused")
	public static @NotNull String convertUnixTimeToDate(long unixTimestamp) {
		return convertUnixTimeToDate(unixTimestamp, "UTC");
	}

	@SuppressWarnings("unused")
	public static @NotNull String convertUnixTimeToDate(long unixTimestamp, String timezone) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z")
				.withZone(ZoneId.of(timezone));
		return formatter.format(Instant.ofEpochMilli(unixTimestamp));
	}

	@Contract(pure = true)
	@SuppressWarnings("unused")
	public static @NotNull String convertToPeriod(long milliseconds) {
		if (milliseconds < 0) {
			return "0s";
		}

		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;

		if (days > 0) {
			return days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
		}

		if (hours > 0) {
			return hours + "h " + minutes + "m " + seconds + "s ";
		}

		if (minutes > 0) {
			return minutes + "m " + seconds + "s ";
		}

		if (seconds > 0) {
			return seconds + "s ";
		}

		return milliseconds + "ms ";
	}

}
