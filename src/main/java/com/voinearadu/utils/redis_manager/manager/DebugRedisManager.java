package com.voinearadu.utils.redis_manager.manager;

import com.google.gson.Gson;
import com.voinearadu.utils.event_manager.EventManager;
import com.voinearadu.utils.generic.dto.Holder;
import com.voinearadu.utils.lambda.lambda.ReturnLambdaExecutor;
import com.voinearadu.utils.logger.Logger;
import com.voinearadu.utils.redis_manager.dto.RedisConfig;
import com.voinearadu.utils.redis_manager.dto.RedisResponse;
import com.voinearadu.utils.redis_manager.event.RedisRequest;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

@Getter
public class DebugRedisManager extends RedisManager {

    private final List<String> channels;

    public DebugRedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, EventManager eventManager, boolean debug, boolean localOnly, List<String> channels) {
        super(gsonProvider, redisConfig, classLoader, eventManager, debug, localOnly);
        this.channels = channels;
    }

    @Override
    public <T> RedisResponse<T> send(RedisRequest<T> event) {
        Logger.log("Cannot sent events from DebugRedisManager");
        return new RedisResponse<>(this, 0);
    }

    @Override
    protected void subscribe() {
        subscriberJedisPubSub = new JedisPubSub() {

            public void onMessage(String channel, final String command) {
                try {
                    onMessageReceive(channel, command);
                } catch (Throwable throwable) {
                    Logger.error("There was an error while receiving a message from Redis.");
                    Logger.error(throwable);
                }
            }

            public void onMessageReceive(String channel, final String event) {
                getDebugger().receive(channel, event);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                getDebugger().subscribed(channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                getDebugger().unsubscribed(channel);
            }
        };

        startRedisThread();
    }

    @Override
    protected String[] getChannels() {
        return channels.toArray(new String[0]);
    }
}