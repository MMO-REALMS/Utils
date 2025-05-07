package com.raduvoinea.utils.file_manager.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.Logger;

public interface ISerializer {

	String serialize(Object object);

	<T> T deserialize(String json, Class<T> type);

	static ISerializer fromGson(Holder<Gson> gsonHolder) {
		return new ISerializer() {
			@Override
			public String serialize(Object object) {
				return gsonHolder.value().toJson(object);
			}

			@Override
			public <T> T deserialize(String json, Class<T> type) {
				return gsonHolder.value().fromJson(json, type);
			}
		};
	}

	static ISerializer fromObjectMapper(Holder<ObjectMapper> mapperHolder) {
		return new ISerializer() {
			@Override
			public String serialize(Object object) {
				try {
					return mapperHolder.value().writeValueAsString(object);
				} catch (Exception exception) {
					Logger.error(exception);
					throw new RuntimeException(exception);
				}
			}

			@Override
			public <T> T deserialize(String json, Class<T> type) {
				try {
					return mapperHolder.value().readValue(json, type);
				} catch (Exception exception) {
					Logger.error(exception);
					throw new RuntimeException(exception);
				}
			}
		};
	}

}
