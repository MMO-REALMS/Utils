package com.raduvoinea.utils.generic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Time {

	private long amount;
	private Unit unit;

	public long toMilliseconds() {
		return amount * unit.toMilliseconds();
	}

	public long toSeconds() {
		return toMilliseconds() / Unit.SECONDS.toMilliseconds();
	}

	public static Time milliseconds(long amount) {
		return new Time(amount, Unit.MILLISECONDS);
	}

	public static Time seconds(long amount) {
		return Unit.SECONDS.of(amount);
	}

	public static Time minutes(long amount) {
		return Unit.MINUTES.of(amount);
	}

	public static Time hours(long amount) {
		return Unit.HOURS.of(amount);
	}

	public static Time days(long amount) {
		return Unit.DAYS.of(amount);
	}

	public static Time weeks(long amount) {
		return Unit.WEEKS.of(amount);
	}

	public static Time months(long amount) {
		return Unit.MONTHS.of(amount);
	}

	public static Time years(long amount) {
		return Unit.YEARS.of(amount);
	}

	@Override
	public String toString() {
		return detailedMillisecondsToString(toMilliseconds());
	}

	public enum Unit {
		MILLISECONDS(1, "milliseconds", "ms"),
		SECONDS(1000 * MILLISECONDS.ms, "seconds", "s"),
		MINUTES(60 * SECONDS.ms, "minutes", "m"),
		HOURS(60 * MINUTES.ms, "hours", "h"),
		DAYS(24 * HOURS.ms, "days", "d"),
		WEEKS(7 * DAYS.ms, "weeks", "w"),
		MONTHS(30 * DAYS.ms, "months", "M"),
		YEARS(365 * DAYS.ms, "years", "y");

		private static final List<Unit> REVERSED_VALUES;
		private static final java.util.Map<String, Unit> PARSE_MAP;

		static {
			Unit[] values = values();
			List<Unit> reversed = new ArrayList<>(values.length);
			for (int i = values.length - 1; i >= 0; i--) {
				reversed.add(values[i]);
			}
			REVERSED_VALUES = Collections.unmodifiableList(reversed);

			java.util.Map<String, Unit> parseMap = new java.util.HashMap<>(values.length * 2);
			for (Unit unit : values) {
				parseMap.put(unit.shortName, unit);
				parseMap.put(unit.name, unit);
			}
			PARSE_MAP = Collections.unmodifiableMap(parseMap);
		}

		private final long ms;
		private final String name;
		private final String shortName;

		Unit(long ms, String name, String shortName) {
			this.ms = ms;
			this.name = name;
			this.shortName = shortName;
		}

		public Time of(long amount) {
			return new Time(amount, this);
		}

		public long toMilliseconds() {
			return ms;
		}

		public static List<Unit> getReversedValues() {
			return REVERSED_VALUES;
		}

		public static Unit parseUnit(String suffix) {
			return PARSE_MAP.get(suffix);
		}
	}

	public static @Nullable Time parse(@NotNull String timeString) {
		if (timeString.endsWith("ms")) {
			try {
				long amount = Long.parseLong(timeString.substring(0, timeString.length() - 2));
				return Time.milliseconds(amount);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		if (timeString.length() < 2) {
			return null;
		}

		long amount;
		try {
			amount = Long.parseLong(timeString.substring(0, timeString.length() - 1));
		} catch (NumberFormatException e) {
			return null;
		}

		Unit unit = Unit.parseUnit(timeString.substring(timeString.length() - 1));
		if (unit != null) {
			return unit.of(amount);
		}

		for (Unit value : Unit.values()) {
			if (timeString.endsWith(value.name)) {
				return value.of(amount);
			}
		}

		return null;
	}

	private static String detailedMillisecondsToString(long milliseconds) {
		StringBuilder sb = new StringBuilder();
		long remaining = milliseconds;
		int parts = 0; // Limit to 3 parts

		for (Unit unit : Unit.getReversedValues()) {
			if (parts >= 3) {
				break;
			}

			long unitAmount = remaining / unit.toMilliseconds();
			if (unitAmount > 0) {
				if (!sb.isEmpty()) {
					sb.append(" ");
				}
				sb.append(unitAmount).append(unit.shortName);
				remaining %= unit.toMilliseconds();
				parts++;
			}
		}

		return sb.isEmpty() ? "0s" : sb.toString();
	}


	public static String millisecondsToString(long milliseconds) {
		for (Unit value : Unit.getReversedValues()) {
			if (value.toMilliseconds() <= milliseconds) {
				long amount = milliseconds / value.toMilliseconds();
				return amount + " " + value.name;
			}
		}

		return "0 milliseconds";
	}

}
