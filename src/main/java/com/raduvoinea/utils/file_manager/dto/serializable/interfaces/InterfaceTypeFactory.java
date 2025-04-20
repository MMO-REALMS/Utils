package com.raduvoinea.utils.file_manager.dto.serializable.interfaces;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;

public class InterfaceTypeFactory implements TypeAdapterFactory {

	private final ClassLoader classLoader;
	private final ReturnArgLambdaExecutor<String, String> classMapper;

	public InterfaceTypeFactory(ClassLoader classLoader, ReturnArgLambdaExecutor<String, String> classMapper) {
		this.classLoader = classLoader;
		this.classMapper = classMapper;
	}

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (!ISerializable.class.isAssignableFrom(type.getRawType())) {
			return null;
		}

		TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
		return new InterfaceTypeAdapter(this, gson, delegate, classLoader, classMapper);
	}


}