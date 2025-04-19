package com.raduvoinea.utils.redis_manager.event;

import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.event_manager.dto.LocalRequest;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Getter
@Setter
public abstract class RedisRequest<Response> extends LocalRequest<Response> {

	private final String className;
	private long id = -1;
	private String originator = "UNKNOWN";
	private String target;

	public RedisRequest(String className, long id, String originator, String target) {
		super(null);
		this.className = className;
		this.id = id;
		this.originator = originator;
		this.target = target;
	}

	public RedisRequest(String target) {
		super(null);
		this.className = getClass().getName();
		this.target = target;
	}

	public CompletableFuture<Response> send() {
		return getRedisManager().send(this)
				.orTimeout(5, TimeUnit.SECONDS); // TODO Config
	}

	public @Nullable Response sendAndGet() {
		try {
			CompletableFuture<Response> future = getRedisManager().send(this);
			return future.get(5, TimeUnit.SECONDS); // TODO Config
		} catch (InterruptedException | ExecutionException | TimeoutException exception) {
			Logger.error(exception);
			return null;
		}
	}

	@Override
	public String toString() {
		return getRedisManager().getGsonHolder().value().toJson(this);
	}

	public String getPublishChannel() {
		return getRedisManager().getRedisConfig().getChannel() + "#" + this.target;
	}

	@Override
	public EventManager getEventManager() {
		return getRedisManager().getEventManagerHolder().value();
	}

	public abstract RedisManager getRedisManager();

	public boolean canRespond() {
		return true;
	}
}
