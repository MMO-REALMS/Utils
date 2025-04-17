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
public class RedisRequest<Response> extends LocalRequest<Response> {

	protected transient RedisManager redisManager;

	private final String className;
	private long id = -1;
	private String originator = "UNKNOWN";
	private String target;

	public RedisRequest(RedisManager redisManager, String className, long id, String originator, String target) {
		super(null);
		this.className = className;
		this.redisManager = redisManager;
		this.id = id;
		this.originator = originator;
		this.target = target;
	}

	public RedisRequest(RedisManager redisManager, String target) {
		super(null);
		this.className = getClass().getName();
		this.redisManager = redisManager;
		this.target = target;
	}

	public static @Nullable RedisRequest<?> deserialize(RedisManager redisManager, String data) {
		RedisRequest<?> event = redisManager.getGsonHolder().value().fromJson(data, RedisRequest.class);

		if (event == null) {
			return null;
		}

		event.setRedisManager(redisManager);
		return event;
	}

	public CompletableFuture<Response> send() {
		return redisManager.send(this)
				.orTimeout(2, TimeUnit.MINUTES); // TODO Config
	}

	public @Nullable Response sendAndGet() {
		try {
			CompletableFuture<Response> future = redisManager.send(this);
			return future.get(2, TimeUnit.MINUTES); // TODO Config
		} catch (InterruptedException | ExecutionException | TimeoutException exception) {
			Logger.error(exception);
			return null;
		}
	}

	@Override
	public String toString() {
		return redisManager.getGsonHolder().value().toJson(this);
	}

	public String getPublishChannel() {
		return redisManager.getRedisConfig().getChannel() + "#" + this.target;
	}

	@Override
	public EventManager getEventManager() {
		return redisManager.getEventManagerHolder().value();
	}
}
