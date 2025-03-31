package com.raduvoinea.utils.redis_manager.dto;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import redis.clients.jedis.params.SetParams;

public interface LockableResource {

    String getRedisLockID();

    RedisManager getRedisManager();

    Time getLockTime();

    default boolean setLock() {
        return getRedisManager().executeOnJedisAndGet(jedis -> {
            if (jedis.exists(getRedisLockID())) {
                return false;
            }

            jedis.set(getRedisLockID(), String.valueOf(System.currentTimeMillis()),
                    SetParams.setParams()
                            .ex(getLockTime().toSeconds())
            );
            return true;
        });
    }

    default void removeLock() {
        getRedisManager().executeOnJedisAndForget(jedis -> {
            jedis.del(getRedisLockID());
        });
    }

    default void withLock(LambdaExecutor executor) {
        withLock(executor, () -> {
        });
    }

    default void withLock(LambdaExecutor executor, LambdaExecutor failExecutor) {
        if (!setLock()) {
            failExecutor.execute();
            return;
        }

        executor.execute();

        removeLock();
    }
}
