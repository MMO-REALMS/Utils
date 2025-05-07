package com.raduvoinea.utils.file_manager.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.raduvoinea.utils.logger.Logger;

public interface ISerializer {

	String serialize(Object object);

	<T> T deserialize(String json, Class<T> type);

	static ISerializer of(Gson gson) {
		return new ISerializer() {
			@Override
			public String serialize(Object object) {
				return gson.toJson(object);
			}

			@Override
			public <T> T deserialize(String json, Class<T> type) {
				return gson.fromJson(json, type);
			}
		};
	}

	static ISerializer of(ObjectMapper mapper) {
		return new ISerializer() {
			@Override
			public String serialize(Object object) {
				try {
					return mapper.writeValueAsString(object);
				} catch (Exception exception) {
					Logger.error(exception);
					throw new RuntimeException(exception);
				}
			}

			@Override
			public <T> T deserialize(String json, Class<T> type) {
				try {
					return mapper.readValue(json, type);
				} catch (Exception exception) {
					Logger.error(exception);
					throw new RuntimeException(exception);
				}
			}
		};
	}

}
