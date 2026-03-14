package com.raduvoinea.utils.lambda;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.no_exception.Lambda;
import com.raduvoinea.utils.lambda.lambda.no_exception.ReturnLambda;
import com.raduvoinea.utils.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScheduleUtils {

	private static final Executor EXECUTOR_POOL = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

	public static @NotNull CancelableTimeTask runTaskLater(@NotNull Lambda executor, Time delay) {
		CancelableTimeTask task = new CancelableTimeTask() {
			@Override
			public void execute() {
				try {
					executor.execute();
				} catch (Throwable throwable) {
					Logger.error(throwable);
				}
			}
		};

		Thread thread = Thread.ofVirtual().start(() -> {
			Timer timer = new Timer();
			timer.schedule(task, delay.toMilliseconds());
		});

		task.setThread(thread);
		return task;
	}

	public static @NotNull CancelableTimeTask runTaskTimer(@NotNull Lambda executor, Time period) {
		CancelableTimeTask task = new CancelableTimeTask() {
			@Override
			public void execute() {
				try {
					executor.execute();
				} catch (Throwable throwable) {
					Logger.error(throwable);
				}
			}
		};

		Thread thread = Thread.ofVirtual().start(() -> {
			Timer timer = new Timer();
			timer.schedule(task, 0, period.toMilliseconds());
		});

		task.setThread(thread);
		return task;
	}

	public static @NotNull CompletableFuture<Void> runTaskAsync(@NotNull Lambda executor) {
		return CompletableFuture.runAsync(() -> {
			try {
				executor.execute();
			} catch (Throwable throwable) {
				Logger.error(throwable);
			}
		}, EXECUTOR_POOL);
	}

	public static @NotNull <R> CompletableFuture<R> runTaskAsync(@NotNull ReturnLambda<R> executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return executor.execute();
			} catch (Throwable throwable) {
				Logger.error(throwable);
				return null;
			}
		}, EXECUTOR_POOL);
	}
}
