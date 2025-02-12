package com.voinearadu.utils.redis_manager.manager;

import com.google.gson.Gson;
import com.voinearadu.utils.event_manager.EventManager;
import com.voinearadu.utils.generic.dto.Holder;
import com.voinearadu.utils.lambda.ScheduleUtils;
import com.voinearadu.utils.lambda.lambda.ArgLambdaExecutor;
import com.voinearadu.utils.lambda.lambda.ReturnArgLambdaExecutor;
import com.voinearadu.utils.logger.Logger;
import com.voinearadu.utils.redis_manager.dto.RedisConfig;
import com.voinearadu.utils.redis_manager.dto.RedisResponse;
import com.voinearadu.utils.redis_manager.event.RedisBroadcast;
import com.voinearadu.utils.redis_manager.event.RedisRequest;
import com.voinearadu.utils.redis_manager.event.impl.ResponseEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RedisManager {

    private static @Getter RedisManager instance;

    private final @Getter Queue<RedisResponse<?>> awaitingResponses;
    private final @Getter EventManager eventManager;
    private final @Getter RedisDebugger debugger;
    private final @Getter RedisConfig redisConfig;
    private final @Getter ClassLoader classLoader;
    private final @Getter boolean localOnly;
    private final @Getter Holder<Gson> gsonHolder;
    protected @Getter JedisPubSub subscriberJedisPubSub;
    private @Getter Thread redisTread;
    private @Getter long id;
    private JedisPool jedisPool;

    @SuppressWarnings("unused")
    public <T> T executeOnJedisAndGet(ReturnArgLambdaExecutor<T, Jedis> executor) {
        return executeOnJedisAndGet(executor, error -> {
        });
    }

    public <T> T executeOnJedisAndGet(ReturnArgLambdaExecutor<T, Jedis> executor, ArgLambdaExecutor<Exception> failExecutor) {
        try (Jedis jedis = jedisPool.getResource()) {
            return executor.execute(jedis);
        } catch (Exception error) {
            Logger.error(error);
            failExecutor.execute(error);
            return null;
        }
    }

    public void executeOnJedisAndForget(ArgLambdaExecutor<Jedis> executor) {
        executeOnJedisAndForget(executor, _ -> {
        });
    }

    public void executeOnJedisAndForget(ArgLambdaExecutor<Jedis> executor, ArgLambdaExecutor<Exception> failExecutor) {
        try (Jedis jedis = jedisPool.getResource()) {
            executor.execute(jedis);
        } catch (Exception error) {
            Logger.error(error);
            failExecutor.execute(error);
        }
    }

    public RedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, EventManager eventManager) {
        this(gsonProvider, redisConfig, classLoader, eventManager, false);
    }

    public RedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, EventManager eventManager, boolean debug) {
        this(gsonProvider, redisConfig, classLoader, eventManager, debug, false);
    }

    public RedisManager(Holder<Gson> gsonHolder, RedisConfig redisConfig, ClassLoader classLoader, EventManager eventManager, boolean debug, boolean localOnly) {
        instance = this;

        this.gsonHolder = gsonHolder;
        this.redisConfig = redisConfig;
        this.localOnly = localOnly;
        this.classLoader = classLoader;

        this.debugger = new RedisDebugger(debug);
        this.debugger.creatingListener(redisConfig.getChannel());
        this.eventManager = eventManager;
        this.awaitingResponses = new ConcurrentLinkedQueue<>();

        if (!localOnly) {
            connectJedis();
            subscribe();
        }
    }

    @SuppressWarnings("unused")
    public void setDebug(boolean debug) {
        debugger.setEnabled(debug);
    }



    private void connectJedis() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }

        JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxTotal(16);

        jedisPool = new JedisPool(
                jedisConfig,
                redisConfig.getHost(),
                redisConfig.getPort(),
                redisConfig.getTimeout(),
                redisConfig.getPassword()
        );
    }

    @Nullable
    private RedisResponse<?> getResponse(ResponseEvent command) {
        //noinspection rawtypes
        for (RedisResponse response : awaitingResponses) {
            if (response.getId() == command.getId()) {
                return response;
            }
        }

        return null;
    }

    protected void subscribe() {
        Logger.log("[RedisManager] Subscribing to Redis: " + redisConfig.getHost() + ":" + redisConfig.getPort() + " @ " + redisConfig.getChannel());

        RedisManager _this = this;
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
                if (event.isEmpty()) {
                    return;
                }

                RedisRequest<?> redisEvent = RedisRequest.deserialize(_this, event);

                if(redisEvent instanceof RedisBroadcast) {
                    if (redisEvent.getTarget().equals(redisEvent.getOriginator())) {
                        // [!] Do not self file broadcast events // TODO Add to java docs of RedisBroadcast
                        return;
                    }
                    return;
                }

                if (redisEvent == null) {
                    Logger.warn("Received invalid RedisEvent: " + event);
                    return;
                }

                if (redisEvent.getClass().equals(ResponseEvent.class)) {
                    ResponseEvent responseEvent = (ResponseEvent) redisEvent;

                    debugger.receiveResponse(channel, event);
                    RedisResponse<?> response = getResponse(responseEvent);
                    if (response == null) {
                        return;
                    }
                    response.respond(responseEvent);

                    return;
                }

                ScheduleUtils.runTaskAsync(() -> {
                    debugger.receive(channel, event);
                    redisEvent.fire();
                });
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                debugger.subscribed(channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                debugger.unsubscribed(channel);
            }
        };


        startRedisThread();
    }

    protected void startRedisThread() {
        if (redisTread != null) {
            redisTread.interrupt();
        }

        redisTread = new Thread(() ->
                executeOnJedisAndForget(jedis -> {
                    debugger.subscribed(redisConfig.getChannel());
                    jedis.subscribe(subscriberJedisPubSub, getChannels());
                }, _ -> {
                    Logger.error("Lost connection to redis server. Retrying in 3 seconds...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {
                    }

                    Logger.good("Reconnecting to redis server.");
                    startRedisThread();
                })
        );
        redisTread.start();
    }

    protected String[] getChannels() {
        return new String[]{redisConfig.getChannel(), redisConfig.getChannelBase() + "#*"};
    }

    public <T> RedisResponse<T> send(@NotNull RedisRequest<T> event) {
        event.setOriginator(redisConfig.getChannel());

        if (event instanceof ResponseEvent) {
            if (event.getTarget().equals(event.getOriginator())) {
                debugger.sendResponse("LOCAL", gsonHolder.value().toJson(event));
                eventManager.fire(event);

                ResponseEvent responseEvent = (ResponseEvent) event;

                RedisResponse<?> response = getResponse(responseEvent);
                if (response == null) {
                    return null;
                }
                response.respond(responseEvent);

                return null;
            }

            debugger.sendResponse(event.getTarget(), gsonHolder.value().toJson(event));

            executeOnJedisAndForget(jedis ->
                    jedis.publish(event.getTarget(), gsonHolder.value().toJson(event))
            );

            return null;
        }

        id++;
        event.setId(id);

        RedisResponse<T> redisResponse = new RedisResponse<>(this, event.getId());
        awaitingResponses.add(redisResponse);

        if (event.getTarget().equals(event.getOriginator())) {
            debugger.send("LOCAL", gsonHolder.value().toJson(event));
            eventManager.fire(event);

            return redisResponse;
        }

        debugger.send(event.getTarget(), gsonHolder.value().toJson(event));

        executeOnJedisAndForget(jedis ->
                jedis.publish(event.getTarget(), gsonHolder.value().toJson(event))
        );

        return redisResponse;
    }
}