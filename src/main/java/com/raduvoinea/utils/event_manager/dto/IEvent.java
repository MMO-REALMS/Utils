package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;

import java.util.concurrent.CompletableFuture;

public interface IEvent<Result> {

	default Result fireSync() {
		return fireSync(false);
	}

	default Result fireSync(boolean suppressExceptions) {
		return this.getEventManager().fireSync(this, suppressExceptions);
	}

	default CompletableFuture<Result> fireAsync() {
		return fireAsync(false);
	}

	default CompletableFuture<Result> fireAsync(boolean suppressExceptions) {
		return this.getEventManager().fireAsync(this, suppressExceptions);
	}

	EventManager getEventManager();

	Result getResult();

}
