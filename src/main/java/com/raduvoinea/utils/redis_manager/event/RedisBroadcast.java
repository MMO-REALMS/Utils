package com.raduvoinea.utils.redis_manager.event;

import com.raduvoinea.utils.lambda.lambda.ArgLambdaExecutor;
import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import com.raduvoinea.utils.redis_manager.dto.RedisResponse;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import org.jetbrains.annotations.Nullable;

public class RedisBroadcast extends RedisEvent {

	public RedisBroadcast(String className, RedisManager redisManager, long id, String originator, String target) {
		super(className, redisManager, id, originator, target);
	}

	public RedisBroadcast(RedisManager redisManager) {
		super(redisManager, "*");
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public RedisResponse<Void> sendAndWait() {
		return send();
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public void sendAndExecuteSync(ArgLambdaExecutor<Void> success, LambdaExecutor fail) {
		send();
		success.execute(null);
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public @Nullable Void sendAndGet(LambdaExecutor fail) {
		send();
		return null;
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public @Nullable Void sendAndGet() {
		send();
		return null;
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public void sendAndExecute(ArgLambdaExecutor<Void> success) {
		send();
		success.execute(null);
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public void sendAndExecute(ArgLambdaExecutor<Void> success, LambdaExecutor fail) {
		send();
		success.execute(null);
	}

	/**
	 * Do not call this method on this type of redis event. This event is only meant to be sent and not listen for any responses.
	 */
	@Override
	@Deprecated(forRemoval = true)
	public RedisResponse<Void> sendAndWait(int timeout) {
		return send();
	}
}
