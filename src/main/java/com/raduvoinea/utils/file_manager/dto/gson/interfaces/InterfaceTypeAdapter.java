package com.raduvoinea.utils.file_manager.dto.gson.interfaces;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;
import lombok.SneakyThrows;

public class InterfaceTypeAdapter<T extends ISerializable> extends TypeAdapter<T> {

	private final Gson gson;
	private final TypeAdapter<T> delegate;
	private final ClassLoader classLoader;
	private final InterfaceTypeFactory interfaceTypeFactory;
	private final ReturnArgLambdaExecutor<String, String> classMapper;

	public InterfaceTypeAdapter(InterfaceTypeFactory interfaceTypeFactory, Gson gson, TypeAdapter<T> delegate,
	                            ClassLoader classLoader, ReturnArgLambdaExecutor<String, String> classMapper) {
		this.interfaceTypeFactory = interfaceTypeFactory;
		this.gson = gson;
		this.delegate = delegate;
		this.classLoader = classLoader;
		this.classMapper=classMapper;
	}

	@Override
	@SneakyThrows
	public void write(JsonWriter out, T value) {
		Gson gson = new Gson();

		out.beginObject();

		out.name("className").value(value.getClass().getName());
		out.name("data");

		gson.toJson(value, value.getClass(), out);

		out.endObject();
	}

	@Override
	@SneakyThrows
	public T read(JsonReader reader) {
		JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

		String className = jsonObject.get("className").getAsString();
		JsonElement dataElement = jsonObject.get("data");

		Class<?> clazz = Class.forName(className);
		Gson gson = new Gson();

		JsonElement actualData;

		if (dataElement.isJsonPrimitive() && dataElement.getAsJsonPrimitive().isString()) {
			actualData = JsonParser.parseString(dataElement.getAsString());
		} else {
			actualData = dataElement;
		}

		Object obj = gson.fromJson(actualData, clazz);

		return (T) obj;
	}

}