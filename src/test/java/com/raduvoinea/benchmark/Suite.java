package com.raduvoinea.benchmark;

import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Suite {

	private final int defaultIterations;
	private final List<Result> results = new ArrayList<>();

	public Suite(int defaultIterations) {
		this.defaultIterations = defaultIterations;
	}

	public Suite run(String name, Lambda task) {
		return run(name, defaultIterations, task);
	}

	public Suite run(String name, int iterations, Lambda task) {
		results.add(Profiler.run(name, iterations, task));
		return this;
	}

	public void print() {
		int width = 120;
		String divider = "-".repeat(width);

		System.out.println();
		System.out.println(divider);
		System.out.printf("  PROFILER RESULTS  (default iterations: %,d)%n", defaultIterations);
		System.out.println(divider);

		Result fastest = results.stream().min(Comparator.comparingLong(Result::averageNs)).orElse(null);

		for (Result result : results) {
			String line = result.toString();
			if (result == fastest) {
				line += "  ← fastest";
			} else {
				double ratio = (double) result.averageNs() / fastest.averageNs();
				line += "  (%.2fx slower)".formatted(ratio);
			}
			System.out.println("  " + line);
		}

		System.out.println(divider);
		System.out.println();
	}
}