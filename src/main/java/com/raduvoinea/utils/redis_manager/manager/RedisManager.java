package com.raduvoinea.utils.redis_manager.manager;

import com.google.gson.Gson;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.ArgLambdaExecutor;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import com.raduvoinea.utils.redis_manager.dto.RedisResponse;
import com.raduvoinea.utils.redis_manager.event.RedisBroadcast;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.event.impl.ResponseEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
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
	private @Getter JedisPool jedisPool;

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
		executeOnJedisAndForget(executor, exception -> {
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

			public void onMessageReceive(String channel, final String eventJson) {
				if (eventJson.isEmpty()) {
					return;
				}

				RedisRequest<?> event = RedisRequest.deserialize(_this, eventJson);

				if (event == null) {
					Logger.warn("Received invalid RedisEvent: " + eventJson);
					return;
				}

				event = switch (event) {
					case RedisBroadcast broadcast -> {
						if (redisConfig.getRedisID().equals(broadcast.getOriginator())) {
							// [!] Do not self file broadcast events // TODO Add to java docs of RedisBroadcast
							yield null;
						}
						yield broadcast;
					}
					case ResponseEvent responseEvent -> {
						debugger.receiveResponse(channel, eventJson);
						RedisResponse<?> response = getResponse(responseEvent);
						if (response == null) {
							yield null;
						}
						response.respond(responseEvent);
						yield null;
					}
					default -> event;
				};

				if (event == null) {
					return;
				}

				RedisRequest<?> finalEvent = event;
				ScheduleUtils.runTaskAsync(() -> {
					debugger.receive(channel, eventJson);
					finalEvent.fire();
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

		Logger.log(new MessageBuilder("[RedisManager] Starting Redis {host}:{port} thread for channels {channels}")
				.parse("host", redisConfig.getHost())
				.parse("port", redisConfig.getPort())
				.parse("channels", Arrays.toString(getChannels()))
		);

		redisTread = new Thread(() ->
				executeOnJedisAndForget(jedis -> {
					jedis.subscribe(subscriberJedisPubSub, getChannels());
				}, exception -> {
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
		return new String[]{redisConfig.getChannel() + "#" + redisConfig.getRedisID(), redisConfig.getChannel() + "#*"};
	}

	public <T> RedisResponse<T> send(@NotNull RedisRequest<T> event) {
		event.setOriginator(redisConfig.getRedisID());

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

			debugger.sendResponse(event.getPublishChannel(), gsonHolder.value().toJson(event));

			executeOnJedisAndForget(jedis ->
					jedis.publish(event.getPublishChannel(), gsonHolder.value().toJson(event))
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

		debugger.send(event.getPublishChannel(), gsonHolder.value().toJson(event));

		executeOnJedisAndForget(jedis ->
				jedis.publish(event.getPublishChannel(), gsonHolder.value().toJson(event))
		);

		return redisResponse;
	}
}