package com.raduvoinea.utils.redis_manager.dto.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;

import java.lang.reflect.Type;


@SuppressWarnings("rawtypes")
public class RedisEventGsonTypeAdapter extends GsonTypeAdapter<RedisRequest> {

	private final RedisManager redisManager;

	public RedisEventGsonTypeAdapter(ClassLoader classLoader, RedisManager redisManager) {
		super(classLoader, RedisRequest.class);
		this.redisManager = redisManager;
	}

	@Override
	public RedisRequest deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		try {
			boolean __RedisEventTypeAdapter = json.getAsJsonObject().has("__RedisRequestTypeAdapter#deserialize");
			String className = json.getAsJsonObject().get("className").getAsString();

			if (!__RedisEventTypeAdapter && !className.equals(RedisRequest.class.getName())) {
				Class<? extends RedisRequest<?>> clazz;

				try {
					//noinspection unchecked
					clazz = (Class<? extends RedisRequest<?>>) classLoader.loadClass(className);
				} catch (Throwable throwable) {
					Logger.error("Class " + className + " was not found in the current JVM context. Please make sure" +
							"the exact class exists in the project. If you want to have different classes in the sender and " +
							"receiver override RedisEvent#getClassName and specify the class name there.");
					return null;
				}

				JsonObject object = json.getAsJsonObject();
				object.addProperty("__RedisRequestTypeAdapter#deserialize", true);
				return context.deserialize(json, clazz);
			}

			long id = json.getAsJsonObject().get("id").getAsLong();
			String originator = json.getAsJsonObject().get("originator").getAsString();
			String redisTarget = json.getAsJsonObject().get("redisTarget").getAsString();

			return new RedisRequest(redisManager, className, id, originator, redisTarget);
		} catch (Exception e) {
			Logger.error("Error while deserializing RedisEvent");
			Logger.error("Json:");
			Logger.error(json.toString());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JsonElement serialize(RedisRequest src, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty("className", src.getClassName());
		object.addProperty("id", src.getId());
		object.addProperty("originator", src.getOriginator());
		object.addProperty("redisTarget", src.getTarget());

		return object;
	}
}
