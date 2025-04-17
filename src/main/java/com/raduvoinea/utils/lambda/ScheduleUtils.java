package com.raduvoinea.utils.lambda;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import com.raduvoinea.utils.lambda.lambda.ReturnLambdaExecutor;
import com.raduvoinea.utils.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScheduleUtils {

	private static final Executor EXECUTOR_POOL = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

	public static @NotNull CancelableTimeTask runTaskTimerAsync(@NotNull LambdaExecutor executor, Time period) {
		CancelableTimeTask task = new CancelableTimeTask() {
			@Override
			public void execute() {
				executor.execute();
			}
		};

		Thread thread = Thread.ofVirtual().start(() -> {
			Timer timer = new Timer();
			timer.schedule(task, 0, period.toMilliseconds());
		});

		task.setThread(thread);
		return task;
	}

	public static @NotNull CompletableFuture<Void> runTaskAsync(@NotNull LambdaExecutor executor) {
		return CompletableFuture.runAsync(()->{
			try {
				executor.execute();
			} catch (Exception exception) {
				Logger.error(exception);
			}
		}, EXECUTOR_POOL);
	}

	public static @NotNull <R> CompletableFuture<R> runTaskAsync(@NotNull ReturnLambdaExecutor<R> executor) {
		return CompletableFuture.supplyAsync(executor::execute, EXECUTOR_POOL);
	}
}
