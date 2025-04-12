package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;

import java.lang.reflect.Type;


@SuppressWarnings({"rawtypes", "unchecked"})
public class SerializableObjectTypeAdapter extends GsonTypeAdapter<SerializableObject> {

	private static final String CLASS_NAME = "class_name";
	private static final String DATA = "data";

	public SerializableObjectTypeAdapter(ClassLoader classLoader) {
		super(classLoader, SerializableObject.class);
	}

	@Override
	public SerializableObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement jsonData = jsonObject.get(DATA);
		String className = jsonObject.get(CLASS_NAME).getAsString();

		if (className == null) {
			return new SerializableObject(null);
		}

		try {
			Class<?> clazz = classLoader.loadClass(className);
			Object object = context.deserialize(jsonData, clazz);

			return new SerializableObject(object);
		} catch (ClassNotFoundException exception) {
			Logger.error(exception);
			return new SerializableObject(null);
		}
	}

	@Override
	public JsonElement serialize(SerializableObject object, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty(CLASS_NAME, object.getObjectClass().getName());
		jsonObject.add(DATA, context.serialize(object.getObject()));

		return jsonObject;
	}
}
