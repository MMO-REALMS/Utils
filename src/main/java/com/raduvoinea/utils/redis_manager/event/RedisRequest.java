package com.raduvoinea.utils.redis_manager.event;

import com.raduvoinea.utils.event_manager.dto.IEvent;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.ArgLambdaExecutor;
import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import com.raduvoinea.utils.redis_manager.dto.RedisResponse;
import com.raduvoinea.utils.redis_manager.event.impl.ResponseEvent;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class RedisRequest<Response> implements IEvent {

	protected transient RedisManager redisManager;

	private final String className;
	private long id = -1;
	private String originator = "UNKNOWN";
	private String target;

	public RedisRequest(RedisManager redisManager, String className, long id, String originator, String target) {
		this.className = className;
		this.redisManager = redisManager;
		this.id = id;
		this.originator = originator;
		this.target = target;
	}

	public RedisRequest(RedisManager redisManager, String target) {
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

	/**
	 * Sends the event locally only
	 * Do NOT call manually, call {@link #sendAndWait()} or any of it derivatives
	 */
	public void fire() {
		redisManager.getEventManager().fire(this);
	}

	@Override
	public void fire(boolean suppressExceptions) {
		redisManager.getEventManager().fire(this, suppressExceptions);
	}

	@Override
	public String toString() {
		return redisManager.getGsonHolder().value().toJson(this);
	}

	public void respond(Response response) {
		new ResponseEvent(redisManager, this, response).send();
	}

	public RedisResponse<Response> send() {
		return redisManager.send(this);
	}

	public void sendAndExecuteSync(ArgLambdaExecutor<Response> success, LambdaExecutor fail) {
		RedisResponse<Response> response = this.sendAndWait();

		if (response.hasTimeout()) {
			fail.execute();
			return;
		}

		success.execute(response.getResponse());
	}

	public @Nullable Response sendAndGet(LambdaExecutor fail) {
		RedisResponse<Response> response = this.sendAndWait();

		if (response.hasTimeout()) {
			fail.execute();
			return null;
		}

		return response.getResponse();
	}

	public @Nullable Response sendAndGet() {
		return sendAndGet(() -> {
		});
	}

	public void sendAndExecute(ArgLambdaExecutor<Response> success) {
		sendAndExecute(success, () -> {
		});
	}

	public void sendAndExecute(ArgLambdaExecutor<Response> success, LambdaExecutor fail) {
		ScheduleUtils.runTaskAsync(() -> sendAndExecuteSync(success, fail));
	}

	@SneakyThrows
	public RedisResponse<Response> sendAndWait() {
		return sendAndWait(redisManager.getRedisConfig().getTimeout());
	}

	@SneakyThrows
	public RedisResponse<Response> sendAndWait(int timeout) {
		int currentWait = 0;
		RedisResponse<Response> response = send();
		while (!response.isFinished()) {
			//noinspection BusyWait
			Thread.sleep(redisManager.getRedisConfig().getWaitBeforeIteration());
			currentWait += redisManager.getRedisConfig().getWaitBeforeIteration();
			if (currentWait > timeout) {
				response.timeout();
				break;
			}
		}

		redisManager.getAwaitingResponses().remove(response);
		return response;
	}

	public String getPublishChannel() {
		return redisManager.getRedisConfig().getChannel() + "#" + this.target;
	}

}
