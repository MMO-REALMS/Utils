package com.raduvoinea.benchmark;

import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;

public class Profiler {

	public static Result run(String name, int iterations, Lambda task) {
		int warmup = Math.max(1_000, iterations / 10);
		for (int i = 0; i < warmup; i++) {
			task.run();
		}

		long start = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			task.run();
		}
		long total = System.nanoTime() - start;

		Result result = new Result(name, iterations, total);
		System.out.println(result);
		return result;
	}

	public static Suite suite(int defaultIterations) {
		return new Suite(defaultIterations);
	}
}