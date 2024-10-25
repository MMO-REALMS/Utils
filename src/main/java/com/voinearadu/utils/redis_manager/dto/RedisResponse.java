package com.voinearadu.utils.redis_manager.dto;

import com.voinearadu.utils.redis_manager.event.impl.ResponseEvent;
import com.voinearadu.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;

@Getter
public class RedisResponse<T> {

    protected final RedisManager redisManager;

    private final long id;
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

    @SuppressWarnings("unused")
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
        T object = (T) redisManager.getGson().execute().fromJson(response.getResponse(), response.getResponseClass());
        respond(object, response.getResponseClassName());
    }

    @SuppressWarnings("unused")
    public T getResponse() {
        return response;
    }

    @SuppressWarnings({"unchecked", "unused"})
    @SneakyThrows
    public Class<T> getResponseClass() {
        if (responseClassName == null) {
            return null;
        }

        return (Class<T>) redisManager.getClassLoader().loadClass(responseClassName);
    }

    @Override
    public String toString() {
        return redisManager.getGson().execute().toJson(this);
    }

}
