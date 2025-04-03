package com.raduvoinea.redis_manager.dto.event_serialization;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;

import java.util.List;

@Getter
public class ComplexEvent1 extends RedisRequest<List<String>> {

	private final List<String> a;
	private final String b;

	public ComplexEvent1(RedisManager eventManager, List<String> a, String b) {
		super(eventManager, eventManager.getRedisConfig().getRedisID());

		this.a = a;
		this.b = b;
	}

}