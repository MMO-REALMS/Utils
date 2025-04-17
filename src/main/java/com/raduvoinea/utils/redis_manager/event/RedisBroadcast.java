package com.raduvoinea.utils.redis_manager.event;

import com.raduvoinea.utils.redis_manager.manager.RedisManager;

public class RedisBroadcast extends RedisRequest<Void> {

	public RedisBroadcast(RedisManager redisManager,String className, long id, String originator, String target) {
		super(redisManager,className, id, originator, target);
	}

	public RedisBroadcast(RedisManager redisManager) {
		super(redisManager, "*");
	}
}
