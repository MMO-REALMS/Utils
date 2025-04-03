package com.raduvoinea.redis_manager.dto.event_serialization;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;

@Getter
public class SimpleEvent1 extends RedisRequest<Integer> {

	private final int a;
	private final int b;

	public SimpleEvent1(RedisManager eventManager, int a, int b) {
		super(eventManager, eventManager.getRedisConfig().getRedisID());

		this.a = a;
		this.b = b;
	}

}
