package com.raduvoinea.utils.generic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Time {

    private long amount;
    private Unit unit;

    public long toMilliseconds() {
        return amount * unit.toMilliseconds();
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

    public static Time range(Time min, Time max) {
        return new TimeRange(min, max);
    }

    @Override
    public String toString() {
        return millisecondsToString(toMilliseconds());
    }

    public enum Unit {
        MILLISECONDS(1, "milliseconds", "ms"),
        SECONDS(1000 * MILLISECONDS.ms, "seconds", "s"),
        MINUTES(60 * SECONDS.ms, "minutes", "m"),
        HOURS(60 * MINUTES.ms, "hours", "h"),
        DAYS(24 * HOURS.ms, "days", "d"),
        WEEKS(7 * DAYS.ms, "weeks", "w"),
        MONTHS(30 * DAYS.ms, "months", "m"),
        YEARS(365 * DAYS.ms, "years", "y");

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

        long amount;

        try {
            amount = Long.parseLong(timeString.substring(0, timeString.length() - 1));
        } catch (NumberFormatException e) {
            return null;
        }

        for (Unit value : Unit.values()) {
            if (timeString.endsWith(value.shortName) || timeString.endsWith(value.name)) {
                return value.of(amount);
            }
        }

        return null;
    }

    public static String millisecondsToString(long milliseconds) {
        for (int i = Unit.values().length - 1; i >= 0; i--) {
            Unit value = Unit.values()[i];

            if (value.toMilliseconds() < milliseconds) {
                long amount = milliseconds / value.toMilliseconds();
                return amount + " " + value.name;
            }
        }

        return "0 milliseconds";
    }

}
