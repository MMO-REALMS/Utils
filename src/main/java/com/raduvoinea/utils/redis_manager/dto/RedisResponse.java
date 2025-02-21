package com.raduvoinea.utils.redis_manager.dto;

import com.raduvoinea.utils.redis_manager.event.impl.ResponseEvent;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;

@Getter
public class RedisResponse<T> {

    protected final RedisManager redisManager;

    private final long id;
    @Getter
    private T response;
    private String responseClassName;

    // State
    private boolean finished = false;
    private boolean timeout = false;

    public RedisResponse(RedisManager redisManager, long id) {
        this.redisManager = redisManager;
        this.id = id;
    }

    public void markAsFinished() {
        finished = true;
    }

    public void timeout() {
        timeout = true;
    }

    public boolean hasTimeout() {
        return timeout;
    }

    public void respond(T object, String responseClass) {
        this.response = object;
        this.responseClassName = responseClass;
        markAsFinished();
    }

    public void respond(ResponseEvent response) {
        if (response.getResponse().isEmpty() || response.getResponseClassName().isEmpty()) {
            respond(null, response.getResponseClassName());
            return;
        }

        if (response.getResponseClass().isAssignableFrom(List.class)) {
            //noinspection unchecked
            T object = (T) response.deserialize();
            respond(object, response.getResponseClassName());
            return;
        }

        //noinspection unchecked
        T object = (T) redisManager.getGsonHolder().value().fromJson(response.getResponse(), response.getResponseClass());
        respond(object, response.getResponseClassName());
    }

    @SuppressWarnings("unused")
    @SneakyThrows
    public Class<T> getResponseClass() {
        if (responseClassName == null) {
            return null;
        }

        //noinspection unchecked
        return (Class<T>) redisManager.getClassLoader().loadClass(responseClassName);
    }

    @Override
    public String toString() {
        return redisManager.getGsonHolder().value().toJson(this);
    }

}
