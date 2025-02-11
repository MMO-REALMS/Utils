package com.voinearadu.utils.generic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Time {

    public int amount;
    public Unit unit;

    public long toMilliseconds() {
        return amount * unit.toMilliseconds();
    }

    @SuppressWarnings("unused")
    public static Time milliseconds(int amount) {
        return new Time(amount, Unit.MILLISECONDS);
    }

    public static Time seconds(int amount) {
        return Unit.SECONDS.of(amount);
    }

    public static Time minutes(int amount) {
        return Unit.MINUTES.of(amount);
    }

    public static Time hours(int amount) {
        return Unit.HOURS.of(amount);
    }

    @SuppressWarnings("unused")
    public static Time days(int amount) {
        return Unit.DAYS.of(amount);
    }

    @SuppressWarnings("unused")
    public static Time weeks(int amount) {
        return Unit.WEEKS.of(amount);
    }

    @SuppressWarnings("unused")
    public static Time months(int amount) {
        return Unit.MONTHS.of(amount);
    }

    @SuppressWarnings("unused")
    public static Time years(int amount) {
        return Unit.YEARS.of(amount);
    }

    public enum Unit {
        MILLISECONDS(1),
        SECONDS(1000 * MILLISECONDS.ms),
        MINUTES(60 * SECONDS.ms),
        HOURS(60 * MINUTES.ms),
        DAYS(24 * HOURS.ms),
        WEEKS(7 * DAYS.ms),
        MONTHS(30 * DAYS.ms),
        YEARS(365 * DAYS.ms);

        private final long ms;

        Unit(long ms) {
            this.ms = ms;
        }

        public Time of(int amount) {
            return new Time(amount, this);
        }

        public long toMilliseconds() {
            return ms;
        }
    }

}
