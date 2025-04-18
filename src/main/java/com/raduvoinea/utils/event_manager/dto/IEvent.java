package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.logger.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface IEvent<Result> {

	default CompletableFuture<Result> fire() {
		return fire(false);
	}

	default CompletableFuture<Result> fire(boolean suppressExceptions) {
		return this.getEventManager().fire(this, suppressExceptions);
	}

	EventManager getEventManager();

	default @Nullable Result fireAndWait() {
		try {
			CompletableFuture<Result> future = fire(false);
			return future.get(2, TimeUnit.MINUTES); // TODO Config
		} catch (InterruptedException | ExecutionException | TimeoutException exception) {
			Logger.error(exception);
			return null;
		}
	}

	Result getResult();

}
