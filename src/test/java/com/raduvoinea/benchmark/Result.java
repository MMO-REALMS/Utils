package com.raduvoinea.benchmark;

import org.jetbrains.annotations.NotNull;

public record Result(String name, int iterations, long totalNs) {

	public long averageNs() {
		return totalNs / iterations;
	}

	public double totalMs() {
		return totalNs / 1_000_000.0;
	}

	public double averageUs() {
		return averageNs() / 1_000.0;
	}

	@Override
	public @NotNull String toString() {
		return "%-60s | iterations: %,10d | total: %,10.2f ms | avg: %,10.3f µs".formatted(
				name, iterations, totalMs(), averageUs()
		);
	}
}