package com.raduvoinea.utils.file_manager.dto.serializable.interfaces;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import lombok.SneakyThrows;

public class InterfaceTypeAdapter<T extends ISerializable> extends TypeAdapter<T> {

	private final Gson gson;
	private final TypeAdapter<T> delegate;
	private final ClassLoader classLoader;
	private final InterfaceTypeFactory interfaceTypeFactory;

	public InterfaceTypeAdapter(InterfaceTypeFactory interfaceTypeFactory, Gson gson, TypeAdapter<T> delegate, ClassLoader classLoader) {
		this.interfaceTypeFactory = interfaceTypeFactory;
		this.gson = gson;
		this.delegate = delegate;
		this.classLoader = classLoader;
	}

	@Override
	public void write(JsonWriter out, T value) {
		SerializableInterface heldInterface = new SerializableInterface(value.getClass().getName(), delegate.toJson(value));
		gson.toJson(heldInterface, SerializableInterface.class, out);
	}

	@SneakyThrows
	@Override
	public T read(JsonReader in) {
		SerializableInterface heldInterface = gson.fromJson(in, SerializableInterface.class);

		if (heldInterface.getClassName() == null || heldInterface.getData() == null) {
			return null;
		}

		//noinspection unchecked
		Class<? extends T> clazz = (Class<? extends T>) classLoader.loadClass(heldInterface.getClassName());
		TypeAdapter<? extends T> localDelegate = gson.getDelegateAdapter(this.interfaceTypeFactory, TypeToken.get(clazz));

		return localDelegate.fromJson(heldInterface.getData());
	}
}