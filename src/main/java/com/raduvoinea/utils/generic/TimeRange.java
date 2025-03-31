package com.raduvoinea.utils.generic;

import java.util.concurrent.ThreadLocalRandom;

public class TimeRange extends Time {
    private final Time min;
    private final Time max;

    public TimeRange(Time min, Time max) {
        // Convert both times to milliseconds
        long minMs = min.toMilliseconds();
        long maxMs = max.toMilliseconds();

        // Compare in milliseconds
        if (minMs > maxMs) {
            throw new IllegalArgumentException("Minimal value should be lesser or equal to maximum value.");
        }

        this.min = min;
        this.max = max;
    }

    public Time getRandom(Unit unit) {
        long minMs = min.toMilliseconds();
        long maxMs = max.toMilliseconds();

        long randomMs = ThreadLocalRandom.current().nextLong(minMs, maxMs + 1);

        long amount = randomMs / unit.toMilliseconds();
        return new Time(amount, unit);
    }
}
