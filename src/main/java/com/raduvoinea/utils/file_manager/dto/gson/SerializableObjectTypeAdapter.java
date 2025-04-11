package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;

import java.lang.reflect.Type;


@SuppressWarnings("rawtypes")
@Deprecated(forRemoval = true)
public class SerializableObjectTypeAdapter extends GsonTypeAdapter<SerializableObject> {

	private static final String CLASS_NAME = "class_name";
	private static final String CLASS_NAME2 = "className";
	private static final String DATA = "data";

	public SerializableObjectTypeAdapter(ClassLoader classLoader) {
		super(classLoader, SerializableObject.class);
	}

	@Override
	public SerializableObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		String className;

		if (json.getAsJsonObject().has(CLASS_NAME)) {
			className = json.getAsJsonObject().get(CLASS_NAME).getAsString();
		} else {
			className = json.getAsJsonObject().get(CLASS_NAME2).getAsString();
		}

		try {
			Class clazz = classLoader.loadClass(className);

			JsonElement jsonData = json.getAsJsonObject().get(DATA);

			Object object = context.deserialize(jsonData, clazz);

			//noinspection unchecked
			return new SerializableObject(clazz, object);
		} catch (ClassNotFoundException error) {
			Logger.error(error);
			return null;
		}
	}

	@Override
	public JsonElement serialize(SerializableObject object, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty(CLASS_NAME, object.objectClass().getName());
		jsonObject.add(DATA, context.serialize(object.object()));

		return jsonObject;
	}
}
