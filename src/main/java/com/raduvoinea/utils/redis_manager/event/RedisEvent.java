package com.raduvoinea.utils.redis_manager.event;

import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisEvent extends RedisRequest<Void> {

	public RedisEvent(RedisManager redisManager, String className, long id, String originator, String target) {
		super(redisManager, className, id, originator, target);
	}

	public RedisEvent(RedisManager redisManager, String target) {
		super(redisManager, target);
	}

}
