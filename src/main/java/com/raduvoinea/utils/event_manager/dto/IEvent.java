package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;
import com.raduvoinea.utils.logger.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IEvent<Result> {

	@Deprecated(forRemoval = true)
	default Result fireSync() {
		return fireSync(false);
	}

	@Deprecated(forRemoval = true)
	default Result fireSync(boolean suppressExceptions) {
		return fireAndGet(suppressExceptions);
	}

	@Deprecated(forRemoval = true)
	default CompletableFuture<Result> fireAsync() {
		return fireAsync(false);
	}

	@Deprecated(forRemoval = true)
	default CompletableFuture<Result> fireAsync(boolean suppressExceptions) {
		return fire(suppressExceptions);
	}

	default Result fireAndGet(boolean supressExceptions, ReturnArgLambdaExecutor<Result, Exception> exceptionHandler) {
		try {
			return this.getEventManager().fire(this, supressExceptions).get();
		} catch (InterruptedException | ExecutionException exception) {
			Logger.error(exception);
			try {
				return exceptionHandler.execute(exception);
			} catch (Exception e) {
				Logger.error(e);
				return null;
			}
		}
	}

	default Result fireAndGet(boolean supressExceptions) {
		return fireAndGet(supressExceptions, (exception) -> null);
	}

	default Result fireAndGet() {
		return fireAndGet(false);
	}

	default CompletableFuture<Result> fire(boolean supressExceptions) {
		return this.getEventManager().fire(this, supressExceptions);
	}

	default CompletableFuture<Result> fire() {
		return fire(false);
	}

	EventManager getEventManager();

	Result getResult();

}
